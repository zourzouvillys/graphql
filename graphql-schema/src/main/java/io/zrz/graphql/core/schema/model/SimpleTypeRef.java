package io.zrz.graphql.core.schema.model;

import io.zrz.graphql.core.schema.TypeRefVisitors.GenericTypRefReturnVisitor;
import lombok.Getter;

public class SimpleTypeRef<T extends Type> implements TypeRef<T> {

  @Getter
  private final T type;

  @Getter
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

  @Override
  public Type getRawType() {
    return this.type;
  }

}
