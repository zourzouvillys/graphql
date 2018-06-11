package io.zrz.graphql.core.doc;

import java.util.List;

import org.immutables.value.Value;

@Value.Immutable(copy = true)
public abstract class GQLFragmentSpreadSelection implements GQLSelection {

  public abstract String name();

  public abstract List<GQLDirective> directives();

  @Override
  public <R> R apply(GQLSelectionVisitor<R> visitor) {
    return visitor.visitFragmentSelection(this);
  }

  public static ImmutableGQLFragmentSpreadSelection.Builder builder() {
    return ImmutableGQLFragmentSpreadSelection.builder();
  }

}
