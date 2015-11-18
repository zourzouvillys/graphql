package io.joss.graphql.core.value;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Singular;
import lombok.ToString;
import lombok.experimental.Wither;

/**
 * A list of {@link GQLValue} instances. A list value doesn't have a type itself, and attempting to get it will throw an exception.
 */

@EqualsAndHashCode
@ToString
@Wither
@Builder(builderClassName = "Builder")
public final class GQLListValue implements GQLValue
{

  @Singular
  private final List<GQLValue> values;

  public List<GQLValue> values()
  {
    return this.values;
  }

  @Override
  public <R> R apply(final GQLValueVisitor<R> visitor)
  {
    return visitor.visitListValue(this);
  }

  /**
   * 
   */

  public static GQLListValue emptyListValue()
  {
    return builder().build();
  }

  /**
   * 
   */

  public static GQLListValue newValueList(GQLValue... values)
  {
    return builder().values(Arrays.asList(values)).build();
  }

  /**
   * Note: toStrings are never used for output to clients. Only degbugging/logging.
   */

  @Override
  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    sb.append("[");
    sb.append(values().stream()
        .map(e -> e.toString())
        .collect(Collectors.joining(",")));
    sb.append(" ]");
    return sb.toString();
  }

}
