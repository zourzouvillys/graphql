package io.joss.graphql.core.types;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import io.joss.graphql.core.decl.GQLInputTypeDeclaration;
import io.joss.graphql.core.decl.GQLInterfaceTypeDeclaration;
import io.joss.graphql.core.decl.GQLObjectTypeDeclaration;
import io.joss.graphql.core.decl.GQLScalarTypeDeclaration;
import io.joss.graphql.core.decl.GQLSchemaDeclaration;
import io.joss.graphql.core.decl.GQLTypeDeclaration;
import io.joss.graphql.core.decl.GQLUnionTypeDeclaration;
import io.joss.graphql.core.doc.GQLDirective;
import lombok.experimental.UtilityClass;

@UtilityClass
public class GQLTypes {

  public static GQLTypeReference ref(GQLTypeDeclaration type) {
    return GQLDeclarationRef.builder().name(type.name()).ref(type).build();
  }

  public static GQLDeclarationRef concreteTypeRef(String typename) {
    return GQLDeclarationRef.builder().name(typename).build();
  }

  /**
   * Constructs a new named scalar type.
   *
   * @param name
   * @return
   */

  public static final GQLScalarTypeDeclaration scalar(final String name) {
    return GQLScalarTypeDeclaration.builder().name(name).build();
  }

  public static GQLDeclarationRef intType() {
    return concreteTypeRef("Int");
  }

  public static GQLTypeReference nonNullStringType() {
    return nonNull(stringType());
  }

  public static GQLDeclarationRef stringType() {
    return typeRef("String");
  }

  public static GQLDeclarationRef idType() {
    return typeRef("ID");
  }

  public static GQLDeclarationRef floatType() {
    return typeRef("Float");
  }

  public static GQLDeclarationRef booleanType() {
    return typeRef("Boolean");
  }

  /**
   * @return The standard GraphQL built-in types.
   */

  public static final List<GQLTypeDeclaration> builtins() {

    final List<GQLTypeDeclaration> types = new LinkedList<>();

    types.add(GQLTypes.scalar("Int"));
    types.add(GQLTypes.scalar("String"));
    types.add(GQLTypes.scalar("Float"));
    types.add(GQLTypes.scalar("Boolean"));
    types.add(GQLTypes.scalar("ID"));

    return types;

  }

  public static GQLInterfaceTypeDeclaration.Builder ifaceBuilder(final String name) {
    return GQLInterfaceTypeDeclaration.builder().name(name);
  }

  public static GQLUnionTypeDeclaration union(final String name, final String... types) {
    return GQLUnionTypeDeclaration.builder().name(name).types(Arrays.stream(types).map(GQLTypes::typeRef).collect(Collectors.toList())).build();
  }

  public static GQLNonNullType nonNull(final String name) {
    return GQLNonNullType.builder().wrappedType(typeRef(name)).build();
  }

  public static GQLTypeReference nonNull(GQLTypeReference type, Collection<GQLDirective> directives) {
    return GQLNonNullType.builder().wrappedType(type).directives(directives).build();
  }

  public static GQLNonNullType nonNull(final GQLTypeReference type) {
    return GQLNonNullType.builder().wrappedType(type).build();
  }

  public static GQLDeclarationRef typeRef(final String name, Collection<GQLDirective> directives) {
    return GQLDeclarationRef.builder().name(name).directives(directives).build();
  }

  public static GQLDeclarationRef typeRef(final String name) {
    return GQLDeclarationRef.builder().name(name).build();
  }

  public static GQLInputTypeDeclaration.Builder inputBuilder(final String name) {
    return GQLInputTypeDeclaration.builder().name(name);
  }

  public static GQLSchemaDeclaration.Builder schemaBuilder() {
    return GQLSchemaDeclaration.builder();
  }

  public static GQLObjectTypeDeclaration.Builder structBuilder(final String name) {
    return GQLObjectTypeDeclaration.builder().name(name);
  }

  public static GQLTypeReference listOf(GQLTypeReference type) {
    return GQLListType.builder().wrappedType(type).build();
  }

  public static GQLTypeReference listOf(GQLTypeReference type, Collection<GQLDirective> directives) {
    return GQLListType.builder().wrappedType(type).directives(directives).build();
  }

}
