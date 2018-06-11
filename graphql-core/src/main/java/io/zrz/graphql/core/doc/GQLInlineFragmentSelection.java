package io.zrz.graphql.core.doc;

import java.util.List;

import org.immutables.value.Value;

import io.zrz.graphql.core.types.GQLDeclarationRef;

@Value.Immutable(copy = true)
public interface GQLInlineFragmentSelection extends GQLSelection {

  GQLDeclarationRef typeCondition();

  List<GQLDirective> directives();

  List<GQLSelection> selections();

  @Override
  default GQLSelectionKind selectionKind() {
    return GQLSelectionKind.INLINE_FRAGMENT;
  }

  @Override
  default <R> R apply(GQLSelectionVisitor<R> visitor) {
    return visitor.visitInlineFragment(this);
  }

  @Override
  default <T, R> R apply(FunctionVisitor<T, R> visitor, T value) {
    return visitor.visitInlineFragment(this, value);
  }

  @Override
  default <T> void apply(ConsumerVisitor<T> visitor, T value) {
    visitor.visitInlineFragment(this, value);
  }

  @Override
  default void apply(VoidVisitor visitor) {
    visitor.visitInlineFragment(this);
  }

  public static ImmutableGQLInlineFragmentSelection.Builder builder() {
    return ImmutableGQLInlineFragmentSelection.builder();
  }

}
