package io.zrz.zulu.schema;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
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
import com.google.common.io.MoreFiles;

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
import io.zrz.zulu.schema.validation.Diagnostic;
import io.zrz.zulu.schema.validation.DiagnosticListener;

/**
 * container for reading multiple schema units (files) and processing into a "compiled unit".
 *
 * @author theo
 *
 */

public class SchemaCompiler implements DiagnosticListener<ResolutionDiagnostic> {

  private final GQLParser parser = new DefaultGQLParser();
  private final List<GQLTypeRegistry> units = new LinkedList<>();
  Multimap<String, GQLTypeDeclaration> names = HashMultimap.create();
  Map<String, ResolvedType> registered = new HashMap<>();

  void addSchema(final GQLTypeRegistry unit) {

    unit.types()
        .stream()
        .forEach(t -> this.names.put(t.name(), t));

    this.units.add(unit);

  }

  public void addUnit(final CharSource source) throws IOException {
    final GQLTypeRegistry unit = this.parser.parseSchema(source.read(), GQLSourceInput.of(source.toString()));
    this.addSchema(unit);
  }

  public void addUnit(final String source) {
    final GQLTypeRegistry unit = this.parser.parseSchema(source, GQLSourceInput.emptySource());
    this.addSchema(unit);
  }

  public ResolvedSchema compile() {
    return new ResolvedSchema(this, ImmutableMap.of());
  }

  public ResolvedSchema compile(final Map<GQLOperationType, String> ops) {
    return new ResolvedSchema(this, ops);
  }

  public ResolvedSchema compile(final String queryRoot) {
    return this.compile(queryRoot, null, null);
  }

  public ResolvedSchema compile(final String queryRoot, final String mutationRoot) {

    return this.compile(queryRoot, mutationRoot, null);
  }

  public ResolvedSchema compile(final String queryRoot, final String mutationRoot, final String subscriptionRoot) {

    final ImmutableMap.Builder<GQLOperationType, String> b = ImmutableMap.builder();

    if (queryRoot != null)
      b.put(GQLOpType.Query, queryRoot);

    if (mutationRoot != null)
      b.put(GQLOpType.Mutation, mutationRoot);

    if (subscriptionRoot != null)
      b.put(GQLOpType.Subscription, subscriptionRoot);

    return this.compile(b.build());
  }

  public TypeUse use(final SchemaElement element, final GQLTypeReference type) {

    final boolean notNull = type.typeRefKind() == GQLTypeRefKind.NOT_NULL;

    switch (type.typeRefKind()) {
      case LIST: {
        final GQLListType listtype = (GQLListType) type;
        final GQLTypeDeclaration res = listtype.apply(GQLTypeVisitors.rootType());
        return new TypeUse(element.schema(), this.build(element.schema(), res.name()), !notNull, 1);
      }
      case DECL:
        break;
      case NOT_NULL:
        break;
      default:
        throw new IllegalArgumentException(type.typeRefKind().name());

    }

    final GQLTypeDeclaration res = type.apply(GQLTypeVisitors.rootType());

    final String typeName = res.name();

    return new TypeUse(element.schema(), this.build(element.schema(), typeName), !notNull, 0);

  }

  public ResolvedType build(final ResolvedSchema schema, final String typeName) {

    if (this.registered.containsKey(typeName)) {
      return this.registered.get(typeName);
    }

    final ArrayList<GQLTypeDeclaration> parts = new ArrayList<>(this.names.get(typeName));

    if (parts.isEmpty()) {
      throw new InvalidSchemaException("missing type '" + typeName + "'");
    }

    final GQLTypeDeclKind typeKind = parts.get(0).typeKind();

    if (!parts.stream().allMatch(s -> s.typeKind() == typeKind)) {
      throw new InvalidSchemaException("mismatched type kinds for '" + typeName + "'");
    }

    if (this.names.containsKey("__OBJECT") && typeKind == GQLTypeDeclKind.OBJECT) {
      parts.addAll(this.names.get("__OBJECT"));
    }

    if (this.names.containsKey("__INTERFACE") && typeKind == GQLTypeDeclKind.INTERFACE) {
      parts.addAll(this.names.get("__INTERFACE"));
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

  public void register(final String typeName, final ResolvedType resolvedType) {
    Preconditions.checkState(!this.registered.containsKey(typeName));
    this.registered.put(typeName, resolvedType);
  }

  @Override
  public void report(final Diagnostic<ResolutionDiagnostic> diag) {
    System.err.println(diag);
  }

  public void addUnit(final Path path) {
    try {
      this.addUnit(MoreFiles.asCharSource(path, StandardCharsets.UTF_8));
    }
    catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

}
