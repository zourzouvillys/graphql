package io.joss.graphql.core.schema.model;

import io.joss.graphql.core.schema.TypeRefVisitors.GenericTypRefReturnVisitor;
import lombok.Getter;

public class GenericTypeRef<T extends Type> implements TypeRef<T> {

  @Getter
  private final TypeRef<T> typeRef;
  @Getter
  private final boolean nullable;

  GenericTypeRef(TypeRef<T> type, boolean nullable) {
    this.typeRef = type;
    this.nullable = nullable;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append("[");
    sb.append(this.typeRef.toString());
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

  @Override
  public Type getRawType() {
    return this.typeRef.getRawType();
  }

}
