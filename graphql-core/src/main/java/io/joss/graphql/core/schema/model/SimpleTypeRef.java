package io.joss.graphql.core.schema.model;

import io.joss.graphql.core.schema.TypeRefVisitors.GenericTypRefReturnVisitor;

public class SimpleTypeRef<T extends Type> implements TypeRef<T> {

  private final T type;
  private final boolean nullable;

  SimpleTypeRef(T type, boolean nullable) {
    this.type = type;
    this.nullable = nullable;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append(this.type.getName());
    if (!this.nullable) {
      sb.append("!");
    }
    return sb.toString();
  }

  @Override
  public <R> R apply(GenericTypRefReturnVisitor<T, R> visitor) {
    return visitor.visitSimpleTypeRef(this);
  }

}
