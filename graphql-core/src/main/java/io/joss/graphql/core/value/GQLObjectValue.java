package io.joss.graphql.core.value;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Singular;
import lombok.experimental.Wither;

@EqualsAndHashCode
@Wither
@Builder(builderClassName = "Builder")
public final class GQLObjectValue implements GQLValue
{

  private static final GQLObjectValue EMPTY = builder().build();

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

  public static GQLObjectValue emptyObjectValue()
  {
    return EMPTY;
  }

  public static GQLObjectValue singleValue(String key, String value)
  {
    return GQLObjectValue.builder().value(key, GQLValues.stringValue(value)).build();
  }

  /**
   * Creates a visitor which when applied to a gQLValue will return the specified field name, if it exists. Otherwise returens null.
   * 
   * @param fieldName
   * @return
   */

  public static GQLValueVisitor<GQLValue> fieldExtractor(String fieldName)
  {

    return new DefaultValueVisitor<GQLValue>() {

      @Override
      public GQLValue visitDefaultValue(GQLValue value)
      {
        return null;
      }

      @Override
      public GQLValue visitObjectValue(GQLObjectValue value)
      {
        return value.entry(fieldName).orElse(null);
      }

    };

  }

}
