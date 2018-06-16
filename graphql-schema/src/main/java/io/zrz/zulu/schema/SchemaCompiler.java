package io.zrz.zulu.schema;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import com.google.common.io.CharSource;

import io.zrz.graphql.core.decl.GQLEnumDeclaration;
import io.zrz.graphql.core.decl.GQLInputTypeDeclaration;
import io.zrz.graphql.core.decl.GQLInterfaceTypeDeclaration;
import io.zrz.graphql.core.decl.GQLObjectTypeDeclaration;
import io.zrz.graphql.core.decl.GQLScalarTypeDeclaration;
import io.zrz.graphql.core.decl.GQLTypeDeclaration;
import io.zrz.graphql.core.decl.GQLUnionTypeDeclaration;
import io.zrz.graphql.core.doc.GQLOpType;
import io.zrz.graphql.core.lang.GQLTypeRegistry;
import io.zrz.graphql.core.lang.GQLTypeVisitors;
import io.zrz.graphql.core.parser.DefaultGQLParser;
import io.zrz.graphql.core.parser.GQLParser;
import io.zrz.graphql.core.parser.GQLSourceInput;
import io.zrz.graphql.core.runtime.GQLOperationType;
import io.zrz.graphql.core.types.GQLListType;
import io.zrz.graphql.core.types.GQLTypeDeclKind;
import io.zrz.graphql.core.types.GQLTypeRefKind;
import io.zrz.graphql.core.types.GQLTypeReference;

/**
 * container for reading multiple schema units (files) and processing into a "compiled unit".
 * 
 * @author theo
 *
 */

public class SchemaCompiler {

  private GQLParser parser = new DefaultGQLParser();
  private List<GQLTypeRegistry> units = new LinkedList<>();
  Multimap<String, GQLTypeDeclaration> names = HashMultimap.create();
  Map<String, ResolvedType> registered = new HashMap<>();

  void addSchema(GQLTypeRegistry unit) {

    unit.types()
        .stream()
        .forEach(t -> names.put(t.name(), t));

    this.units.add(unit);

  }

  public void addUnit(CharSource source) throws IOException {
    GQLTypeRegistry unit = parser.parseSchema(source.read(), GQLSourceInput.of(source.toString()));
    addSchema(unit);
  }

  public ResolvedSchema compile(Map<GQLOperationType, String> ops) {
    return new ResolvedSchema(this, ops);
  }

  public ResolvedSchema compile(String queryRoot) {
    Map<GQLOperationType, String> roots = ImmutableMap.of(GQLOpType.Query, queryRoot);
    return compile(roots);
  }

  public ResolvedSchema compile(String queryRoot, String mutationRoot) {
    Map<GQLOperationType, String> roots = ImmutableMap.of(
        GQLOpType.Query, queryRoot,
        GQLOpType.Mutation, mutationRoot);
    return compile(roots);
  }

  public ResolvedSchema compile(String queryRoot, String mutationRoot, String subscriptionRoot) {
    Map<GQLOperationType, String> roots = ImmutableMap.of(
        GQLOpType.Query, queryRoot,
        GQLOpType.Mutation, mutationRoot,
        GQLOpType.Subscription, subscriptionRoot);
    return compile(roots);
  }

  public TypeUse use(SchemaElement element, GQLTypeReference type) {

    boolean notNull = (type.typeRefKind() == GQLTypeRefKind.NOT_NULL);

    switch (type.typeRefKind()) {
      case LIST: {
        GQLListType listtype = (GQLListType) type;
        GQLTypeDeclaration res = listtype.apply(GQLTypeVisitors.rootType());
        return new TypeUse(element.schema(), build(element.schema(), res.name()), !notNull, 1);
      }
      case DECL:
        break;
      case NOT_NULL:
        break;
      default:
        throw new IllegalArgumentException(type.typeRefKind().name());

    }

    GQLTypeDeclaration res = type.apply(GQLTypeVisitors.rootType());

    String typeName = res.name();

    return new TypeUse(element.schema(), build(element.schema(), typeName), !notNull, 0);

  }

  public ResolvedType build(ResolvedSchema schema, String typeName) {

    if (registered.containsKey(typeName)) {
      return registered.get(typeName);
    }

    ArrayList<GQLTypeDeclaration> parts = new ArrayList<>(names.get(typeName));

    if (parts.isEmpty()) {
      throw new InvalidSchemaException("missing type '" + typeName + "'");
    }

    GQLTypeDeclKind typeKind = parts.get(0).typeKind();

    if (!parts.stream().allMatch(s -> s.typeKind() == typeKind)) {
      throw new InvalidSchemaException("mismatched type kinds for '" + typeName + "'");
    }

    if (names.containsKey("__OBJECT") && (typeKind == GQLTypeDeclKind.OBJECT)) {
      parts.addAll(names.get("__OBJECT"));
    }

    if (names.containsKey("__INTERFACE") && (typeKind == GQLTypeDeclKind.INTERFACE)) {
      parts.addAll(names.get("__INTERFACE"));
    }

    switch (typeKind) {
      case OBJECT:
        return new ResolvedObjectType(this, schema, typeName, typeKind, parts.stream().map(GQLObjectTypeDeclaration.class::cast).collect(Collectors.toList()));
      case INPUT_OBJECT:
        return new ResolvedInputType(this, schema, typeName, typeKind, parts.stream().map(GQLInputTypeDeclaration.class::cast).collect(Collectors.toList()));
      case ENUM:
        return new ResolvedEnumType(this, schema, typeName, typeKind, parts.stream().map(GQLEnumDeclaration.class::cast).collect(Collectors.toList()));
      case INTERFACE:
        return new ResolvedInterfaceType(this, schema, typeName, typeKind,
            parts.stream().map(GQLInterfaceTypeDeclaration.class::cast).collect(Collectors.toList()));
      case SCALAR:
        return new ResolvedScalarType(this, schema, typeName, typeKind, parts.stream().map(GQLScalarTypeDeclaration.class::cast).collect(Collectors.toList()));
      case UNION:
        return new ResolvedUnionType(this, schema, typeName, typeKind, parts.stream().map(GQLUnionTypeDeclaration.class::cast).collect(Collectors.toList()));
      default:
        throw new IllegalArgumentException(typeKind.name());
    }

  }

  public void register(String typeName, ResolvedType resolvedType) {
    Preconditions.checkState(!this.registered.containsKey(typeName));
    this.registered.put(typeName, resolvedType);
  }

}
