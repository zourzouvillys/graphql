package io.zrz.graphql.core.value;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Wither;

@EqualsAndHashCode
@ToString
@Wither
public final class GQLEnumValueRef implements GQLScalarValue {

  private final String value;

  GQLEnumValueRef(final String value) {
    this.value = value;
  }

  public String value() {
    return this.value;
  }

  @Override
  public <R> R apply(final GQLValueVisitor<R> visitor) {
    return visitor.visitEnumValueRef(this);
  }

  public static GQLEnumValueRef from(final String value) {
    return new GQLEnumValueRef(value);
  }

  @Override
  public GQLValueType type() {
    return GQLValueType.Enum;
  }

}
