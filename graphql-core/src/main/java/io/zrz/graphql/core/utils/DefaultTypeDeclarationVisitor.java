package io.zrz.graphql.core.utils;

import io.zrz.graphql.core.decl.GQLTypeDeclaration;

public class DefaultTypeDeclarationVisitor<T> extends AbstractDefaultTypeDeclarationVisitor<T> {

  private final T defaultValue;

  public DefaultTypeDeclarationVisitor(T defaultValue) {
    this.defaultValue = defaultValue;
  }

  @Override
  protected T visitDefault(GQLTypeDeclaration type) {
    return this.defaultValue;
  }

}
