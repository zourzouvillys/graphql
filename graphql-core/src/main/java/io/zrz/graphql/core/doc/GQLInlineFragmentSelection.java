package io.zrz.graphql.core.doc;

import java.util.List;

import org.immutables.value.Value;

import io.zrz.graphql.core.types.GQLDeclarationRef;

@Value.Immutable(copy = true)
public abstract class GQLInlineFragmentSelection implements GQLSelection {

  public abstract GQLDeclarationRef typeCondition();

  public abstract List<GQLDirective> directives();

  public abstract List<GQLSelection> selections();

  @Override
  public <R> R apply(GQLSelectionVisitor<R> visitor) {
    return visitor.visitInlineFragment(this);
  }

  public static ImmutableGQLInlineFragmentSelection.Builder builder() {
    return ImmutableGQLInlineFragmentSelection.builder();
  }

}
