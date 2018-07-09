package io.zrz.graphql.core.value;

import java.util.function.Function;

import org.immutables.value.Value;

@Value.Immutable
@Value.Style(allowedClasspathAnnotations = { Override.class })
public abstract class GQLStringValue implements GQLScalarValue {

  private static final Function<GQLValue, String> STRING_EXTRACTOR_INSTANCE = arg -> arg.apply(GQLValueConverters.stringConverter());

  @Value.Parameter
  public abstract String value();

  @Override
  public <R> R apply(final GQLValueVisitor<R> visitor) {
    return visitor.visitStringValue(this);
  }

  public static GQLStringValue from(final String value) {
    return ImmutableGQLStringValue.of(value);
  }

  /**
   * Note: toStrings are never used for output to clients. Only degbugging/logging.
   */

  @Override
  public String toString() {
    return String.format("\"%s\"", this.value());
  }

  public static Function<GQLValue, String> stringExtractor() {
    return STRING_EXTRACTOR_INSTANCE;
  }

  @Override
  public GQLValueType type() {
    return GQLValueType.String;
  }

}
