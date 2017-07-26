package io.joss.graphql.core.value;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Wither;

@Wither
@ToString
@EqualsAndHashCode
public final class GQLIntValue implements GQLScalarValue {

  private final long value;

  private GQLIntValue(final long value) {
    this.value = value;
  }

  public long value() {
    return this.value;
  }

  @Override
  public <R> R apply(final GQLValueVisitor<R> visitor) {
    return visitor.visitIntValue(this);
  }

  public static GQLIntValue from(final long value) {
    return new GQLIntValue(value);
  }

  @Override
  public GQLValueType type() {
    return GQLValueType.Int;
  }

}
