package io.joss.graphql.core.value;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Singular;
import lombok.ToString;
import lombok.experimental.Wither;

@EqualsAndHashCode
@Wither
@Builder(builderClassName = "Builder")
public final class GQLObjectValue implements GQLValue
{

  @Singular
  private final Map<String, GQLValue> values;

  @Override
  public <R> R apply(final GQLValueVisitor<R> visitor)
  {
    return visitor.visitObjectValue(this);
  }

  public Map<String, GQLValue> entries()
  {
    return this.values;
  }

  public Optional<GQLValue> entry(String key)
  {
    return Optional.ofNullable(values.get(key));
  }

  /**
   * Note: toStrings are never used for output to clients. Only degbugging/logging.
   */

  @Override
  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    sb.append("{");
    sb.append(entries().entrySet().stream()
        .map(e -> String.format(" %s: %s", e.getKey(), e.getValue()))
        .collect(Collectors.joining(",")));
    sb.append(" }");
    return sb.toString();
  }

}
