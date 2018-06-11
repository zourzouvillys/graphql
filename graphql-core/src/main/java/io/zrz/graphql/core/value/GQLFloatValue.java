package io.zrz.graphql.core.value;

import org.immutables.value.Value;

@Value.Immutable
public abstract class GQLFloatValue implements GQLScalarValue {

  @Value.Parameter
  public abstract double value();

  public static GQLFloatValue from(final double value) {
    return ImmutableGQLFloatValue.of(value);
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
