package io.joss.graphql.core.value;

import java.util.function.Function;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Wither;

@Wither
@EqualsAndHashCode
public final class GQLStringValue implements GQLConstantValue
{

  private static final Function<GQLValue, String> STRING_EXTRACTOR_INSTANCE = arg -> arg.apply(GQLValueConverters.stringConverter());
  private final String value;

  public GQLStringValue(final String value)
  {
    this.value = value;
  }

  public String value()
  {
    return this.value;
  }

  @Override
  public <R> R apply(final GQLValueVisitor<R> visitor)
  {
    return visitor.visitStringValue(this);
  }

  public static GQLStringValue from(final String value)
  {
    return new GQLStringValue(value);
  }

  /**
   * Note: toStrings are never used for output to clients. Only degbugging/logging.
   */

  @Override
  public String toString()
  {
    return String.format("\"%s\"", value);
  }

  public static Function<GQLValue, String> stringExtractor()
  {
    return STRING_EXTRACTOR_INSTANCE;
  }

}
