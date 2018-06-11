package io.zrz.graphql.core.types;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import io.zrz.graphql.core.decl.GQLInputTypeDeclaration;
import io.zrz.graphql.core.decl.GQLInterfaceTypeDeclaration;
import io.zrz.graphql.core.decl.GQLScalarTypeDeclaration;
import io.zrz.graphql.core.decl.GQLTypeDeclaration;
import io.zrz.graphql.core.decl.GQLUnionTypeDeclaration;
import io.zrz.graphql.core.decl.ImmutableGQLInputTypeDeclaration;
import io.zrz.graphql.core.decl.ImmutableGQLObjectTypeDeclaration;
import io.zrz.graphql.core.decl.ImmutableGQLSchemaDeclaration;
import io.zrz.graphql.core.doc.GQLDirective;

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
    return ImmutableGQLNonNullType.builder().type(typeRef(name)).build();
  }

  public static GQLTypeReference nonNull(GQLTypeReference type, Collection<GQLDirective> directives) {
    return ImmutableGQLNonNullType.builder().type(type).directives(directives).build();
  }

  public static GQLNonNullType nonNull(final GQLTypeReference type) {
    return ImmutableGQLNonNullType.builder().type(type).build();
  }

  public static GQLDeclarationRef typeRef(final String name, Collection<GQLDirective> directives) {
    return GQLDeclarationRef.builder().name(name).directives(directives).build();
  }

  public static GQLDeclarationRef typeRef(final String name) {
    return GQLDeclarationRef.builder().name(name).build();
  }

  public static ImmutableGQLInputTypeDeclaration.Builder inputBuilder(final String name) {
    return GQLInputTypeDeclaration.builder().name(name);
  }

  public static ImmutableGQLSchemaDeclaration.Builder schemaBuilder() {
    return ImmutableGQLSchemaDeclaration.builder();
  }

  public static ImmutableGQLObjectTypeDeclaration.Builder structBuilder(final String name) {
    return ImmutableGQLObjectTypeDeclaration.builder().name(name);
  }

  public static GQLTypeReference listOf(GQLTypeReference type) {
    return ImmutableGQLListType.builder().type(type).build();
  }

  public static GQLTypeReference listOf(GQLTypeReference type, Collection<GQLDirective> directives) {
    return ImmutableGQLListType.builder().type(type).directives(directives).build();
  }

}
