package io.zrz.graphql.core.utils;

import io.zrz.graphql.core.decl.GQLEnumDeclaration;
import io.zrz.graphql.core.decl.GQLInputTypeDeclaration;
import io.zrz.graphql.core.decl.GQLInterfaceTypeDeclaration;
import io.zrz.graphql.core.decl.GQLObjectTypeDeclaration;
import io.zrz.graphql.core.decl.GQLScalarTypeDeclaration;
import io.zrz.graphql.core.decl.GQLTypeDeclaration;
import io.zrz.graphql.core.decl.GQLTypeDeclarationVisitor;
import io.zrz.graphql.core.decl.GQLUnionTypeDeclaration;

public abstract class AbstractDefaultTypeDeclarationVisitor<T> implements GQLTypeDeclarationVisitor<T> {

  protected abstract T visitDefault(GQLTypeDeclaration type);

  @Override
  public T visitUnion(GQLUnionTypeDeclaration type) {
    return this.visitDefault(type);
  }

  @Override
  public T visitScalar(GQLScalarTypeDeclaration type) {
    return this.visitDefault(type);
  }

  @Override
  public T visitObject(GQLObjectTypeDeclaration type) {
    return this.visitDefault(type);
  }

  @Override
  public T visitInterface(GQLInterfaceTypeDeclaration type) {
    return this.visitDefault(type);
  }

  @Override
  public T visitEnum(GQLEnumDeclaration type) {
    return this.visitDefault(type);
  }

  @Override
  public T visitInput(GQLInputTypeDeclaration type) {
    return this.visitDefault(type);
  }

}
