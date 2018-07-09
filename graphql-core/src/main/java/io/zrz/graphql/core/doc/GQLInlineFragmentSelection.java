package io.zrz.graphql.core.doc;

import java.util.List;

import org.immutables.value.Value;

import io.zrz.graphql.core.types.GQLDeclarationRef;

@Value.Immutable(copy = true)
@Value.Style(allowedClasspathAnnotations = { Override.class })
public interface GQLInlineFragmentSelection extends GQLSelection {

  GQLDeclarationRef typeCondition();

  List<GQLDirective> directives();

  List<GQLSelection> selections();

  @Override
  default GQLSelectionKind selectionKind() {
    return GQLSelectionKind.INLINE_FRAGMENT;
  }

  @Override
  default <R> R apply(final GQLSelectionVisitor<R> visitor) {
    return visitor.visitInlineFragment(this);
  }

  @Override
  default <T, R> R apply(final FunctionVisitor<T, R> visitor, final T value) {
    return visitor.visitInlineFragment(this, value);
  }

  @Override
  default <T> void apply(final ConsumerVisitor<T> visitor, final T value) {
    visitor.visitInlineFragment(this, value);
  }

  @Override
  default void apply(final VoidVisitor visitor) {
    visitor.visitInlineFragment(this);
  }

  static ImmutableGQLInlineFragmentSelection.Builder builder() {
    return ImmutableGQLInlineFragmentSelection.builder();
  }

}
