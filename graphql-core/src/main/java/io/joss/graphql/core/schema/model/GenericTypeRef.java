package io.joss.graphql.core.schema.model;

import io.joss.graphql.core.schema.TypeRefVisitors.GenericTypRefReturnVisitor;

public class GenericTypeRef<T extends Type> implements TypeRef<T> {

  private final TypeRef<T> type;
  private final boolean nullable;

  GenericTypeRef(TypeRef<T> type, boolean nullable) {
    this.type = type;
    this.nullable = nullable;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append("[");
    sb.append(this.type.toString());
    sb.append("]");
    if (!this.nullable) {
      sb.append("!");
    }
    return sb.toString();
  }

  @Override
  public <R> R apply(GenericTypRefReturnVisitor<T, R> visitor) {
    return visitor.visitGenericTypeRef(this);
  }

}
