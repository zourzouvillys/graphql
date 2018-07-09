package io.zrz.graphql.core.doc;

import java.util.List;

import org.immutables.value.Value;

@Value.Immutable(copy = true)
@Value.Style(allowedClasspathAnnotations = { Override.class })
public interface GQLFragmentSpreadSelection extends GQLSelection {

  String name();

  List<GQLDirective> directives();

  @Override
  default GQLSelectionKind selectionKind() {
    return GQLSelectionKind.FRAGMENT_SPREAD;
  }

  @Override
  default <R> R apply(final GQLSelectionVisitor<R> visitor) {
    return visitor.visitFragmentSelection(this);
  }

  @Override
  default <T, R> R apply(final FunctionVisitor<T, R> visitor, final T value) {
    return visitor.visitFragmentSelection(this, value);
  }

  @Override
  default <T> void apply(final ConsumerVisitor<T> visitor, final T value) {
    visitor.visitFragmentSelection(this, value);
  }

  @Override
  default void apply(final VoidVisitor visitor) {
    visitor.visitFragmentSelection(this);
  }

  static ImmutableGQLFragmentSpreadSelection.Builder builder() {
    return ImmutableGQLFragmentSpreadSelection.builder();
  }

}
