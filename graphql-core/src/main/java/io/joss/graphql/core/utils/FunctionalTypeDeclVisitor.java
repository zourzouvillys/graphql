package io.joss.graphql.core.utils;

import java.util.function.Function;

import io.joss.graphql.core.decl.GQLEnumDeclaration;
import io.joss.graphql.core.decl.GQLInputTypeDeclaration;
import io.joss.graphql.core.decl.GQLObjectTypeDeclaration;
import io.joss.graphql.core.decl.GQLTypeDeclaration;
import io.joss.graphql.core.decl.GQLTypeDeclarationVisitor;

public class FunctionalTypeDeclVisitor {

  public static <R> GQLTypeDeclarationVisitor<R> inputType(Function<GQLInputTypeDeclaration, R> found, Function<GQLTypeDeclaration, R> other) {

    return new AbstractDefaultTypeDeclarationVisitor<R>() {

      @Override
      public R visitInput(GQLInputTypeDeclaration type) {
        return found.apply(type);
      }

      @Override
      protected R visitDefault(GQLTypeDeclaration type) {
        return other.apply(type);
      }

    };

  }

  public static <R> GQLTypeDeclarationVisitor<R> objectType(Function<GQLObjectTypeDeclaration, R> found, Function<GQLTypeDeclaration, R> other) {

    return new AbstractDefaultTypeDeclarationVisitor<R>() {

      @Override
      public R visitObject(GQLObjectTypeDeclaration type) {
        return found.apply(type);
      }

      @Override
      protected R visitDefault(GQLTypeDeclaration type) {
        return other.apply(type);
      }

    };

  }

  public static <R> GQLTypeDeclarationVisitor<R> enumType(Function<GQLEnumDeclaration, R> found, Function<GQLTypeDeclaration, R> other) {

    return new AbstractDefaultTypeDeclarationVisitor<R>() {

      @Override
      public R visitEnum(GQLEnumDeclaration type) {
        return found.apply(type);
      }

      @Override
      protected R visitDefault(GQLTypeDeclaration type) {
        return other.apply(type);
      }

    };

  }

}
