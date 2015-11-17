package io.joss.graphql.core.value;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Wither;

@Wither
@ToString
@EqualsAndHashCode
public final class GQLStringValue implements GQLValue
{

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

}
