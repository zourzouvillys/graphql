package io.zrz.graphql.core.utils;

import java.util.function.Function;

import io.zrz.graphql.core.decl.GQLEnumDeclaration;
import io.zrz.graphql.core.decl.GQLInputTypeDeclaration;
import io.zrz.graphql.core.decl.GQLInterfaceTypeDeclaration;
import io.zrz.graphql.core.decl.GQLObjectTypeDeclaration;
import io.zrz.graphql.core.decl.GQLScalarTypeDeclaration;
import io.zrz.graphql.core.decl.GQLTypeDeclaration;
import io.zrz.graphql.core.decl.GQLTypeDeclarationVisitor;

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

  public static <R> GQLTypeDeclarationVisitor<R> interfaceType(Function<GQLInterfaceTypeDeclaration, R> found, Function<GQLTypeDeclaration, R> other) {

    return new AbstractDefaultTypeDeclarationVisitor<R>() {

      @Override
      public R visitInterface(GQLInterfaceTypeDeclaration type) {
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

  public static <R> GQLTypeDeclarationVisitor<R> scalarType(Function<GQLScalarTypeDeclaration, R> found, Function<GQLTypeDeclaration, R> other) {

    return new AbstractDefaultTypeDeclarationVisitor<R>() {

      @Override
      public R visitScalar(GQLScalarTypeDeclaration type) {
        return found.apply(type);
      }

      @Override
      protected R visitDefault(GQLTypeDeclaration type) {
        return other.apply(type);
      }

    };

  }

}
