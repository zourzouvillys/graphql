package io.zrz.graphql.core.value;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.immutables.value.Value;

@Value.Immutable(copy = true)
@Value.Style(allowedClasspathAnnotations = { Override.class })
public abstract class GQLObjectValue implements GQLValue {

  private static final GQLObjectValue EMPTY = builder().build();

  public abstract Map<String, GQLValue> values();

  @Override
  public <R> R apply(final GQLValueVisitor<R> visitor) {
    return visitor.visitObjectValue(this);
  }

  public Map<String, GQLValue> entries() {
    return this.values();
  }

  public Optional<GQLValue> entry(final String key) {
    return Optional.ofNullable(this.values().get(key));
  }

  /**
   *
   * Note: toStrings are never used for output to clients. Only degbugging/logging..
   */

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append("{");
    sb.append(this.entries().entrySet().stream()
        .map(e -> String.format(" %s: %s", e.getKey(), e.getValue()))
        .collect(Collectors.joining(",")));
    sb.append(" }");
    return sb.toString();
  }

  public static GQLObjectValue emptyObjectValue() {
    return EMPTY;
  }

  public static GQLObjectValue singleValue(final String key, final String value) {
    return GQLObjectValue.builder().putValues(key, GQLValues.stringValue(value)).build();
  }

  public static GQLObjectValue singleValue(final String key, final GQLValue value) {
    return GQLObjectValue.builder().putValues(key, value).build();
  }

  /**
   * Creates a visitor which when applied to a gQLValue will return the specified field name, if it exists. Otherwise
   * returens null.
   *
   * @param fieldName
   * @return
   */

  public static GQLValueVisitor<GQLValue> fieldExtractor(final String fieldName) {

    return new DefaultValueVisitor<>() {

      @Override
      public GQLValue visitDefaultValue(final GQLValue value) {
        return null;
      }

      @Override
      public GQLValue visitObjectValue(final GQLObjectValue value) {
        return value.entry(fieldName).orElse(null);
      }

    };

  }

  @Override
  public GQLValueType type() {
    return GQLValueType.Object;
  }

  public static ImmutableGQLObjectValue.Builder builder() {
    return ImmutableGQLObjectValue.builder();
  }

}
