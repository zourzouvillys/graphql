package io.zrz.graphql.core.value;

import org.immutables.value.Value;

@Value.Immutable(copy = false)
@Value.Style(allowedClasspathAnnotations = { Override.class })
public abstract class GQLIntValue implements GQLScalarValue {

  @Value.Parameter
  public abstract long value();

  @Override
  public <R> R apply(final GQLValueVisitor<R> visitor) {
    return visitor.visitIntValue(this);
  }

  public static GQLIntValue from(final long value) {
    return ImmutableGQLIntValue.of(value);
  }

  @Override
  public GQLValueType type() {
    return GQLValueType.Int;
  }

}
