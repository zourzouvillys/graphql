package io.zrz.graphql.core.value;

import org.immutables.value.Value;

@Value.Immutable
@Value.Style(allowedClasspathAnnotations = { Override.class })

public abstract class GQLEnumValueRef implements GQLScalarValue {

  @Value.Parameter
  public abstract String value();

  @Override
  public <R> R apply(final GQLValueVisitor<R> visitor) {
    return visitor.visitEnumValueRef(this);
  }

  public static GQLEnumValueRef from(final String value) {
    return ImmutableGQLEnumValueRef.of(value);
  }

  @Override
  public GQLValueType type() {
    return GQLValueType.Enum;
  }

}
