package io.zrz.graphql.core.value;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Wither;

@EqualsAndHashCode
@ToString
@Wither
public final class GQLFloatValue implements GQLScalarValue {

  private final double value;

  GQLFloatValue(final double value) {
    this.value = value;
  }

  public double value() {
    return this.value;
  }

  public static GQLFloatValue from(final double value) {
    return new GQLFloatValue(value);
  }

  @Override
  public <R> R apply(final GQLValueVisitor<R> visitor) {
    return visitor.visitFloatValue(this);
  }

  @Override
  public GQLValueType type() {
    return GQLValueType.Float;
  }

}
