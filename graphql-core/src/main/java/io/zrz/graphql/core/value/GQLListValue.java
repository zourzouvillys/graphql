package io.zrz.graphql.core.value;

import java.util.List;
import java.util.stream.Collectors;

import org.immutables.value.Value;

/**
 * A list of {@link GQLValue} instances. A list value doesn't have a type itself, and attempting to get it will throw an
 * exception.
 */

@Value.Immutable
public abstract class GQLListValue implements GQLValue {

  private static final GQLListValue EMPTY = builder().build();

  public abstract List<GQLValue> values();

  @Override
  public <R> R apply(final GQLValueVisitor<R> visitor) {
    return visitor.visitListValue(this);
  }

  /**
   *
   */

  public static GQLListValue emptyListValue() {
    return EMPTY;
  }

  /**
   *
   */

  public static GQLListValue newValueList(GQLValue... values) {
    return builder().addValues(values).build();
  }

  /**
   * Note: toStrings are never used for output to clients. Only degbugging/logging.
   */

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append("[ ");
    sb.append(this.values().stream()
        .map(e -> e == null ? "null" : e.toString())
        .collect(Collectors.joining(", ")));
    sb.append(" ]");
    return sb.toString();
  }

  @Override
  public GQLValueType type() {
    return GQLValueType.List;
  }

  public static ImmutableGQLListValue.Builder builder() {
    return ImmutableGQLListValue.builder();
  }

}
