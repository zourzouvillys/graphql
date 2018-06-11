package io.zrz.graphql.core.doc;

import java.util.List;

import org.immutables.value.Value;

@Value.Immutable(copy = true)
public interface GQLFragmentSpreadSelection extends GQLSelection {

  String name();

  List<GQLDirective> directives();

  @Override
  default GQLSelectionKind selectionKind() {
    return GQLSelectionKind.FRAGMENT_SPREAD;
  }

  @Override
  default <R> R apply(GQLSelectionVisitor<R> visitor) {
    return visitor.visitFragmentSelection(this);
  }

  @Override
  default <T, R> R apply(FunctionVisitor<T, R> visitor, T value) {
    return visitor.visitFragmentSelection(this, value);
  }

  @Override
  default <T> void apply(ConsumerVisitor<T> visitor, T value) {
    visitor.visitFragmentSelection(this, value);
  }

  @Override
  default void apply(VoidVisitor visitor) {
    visitor.visitFragmentSelection(this);
  }

  public static ImmutableGQLFragmentSpreadSelection.Builder builder() {
    return ImmutableGQLFragmentSpreadSelection.builder();
  }

}
