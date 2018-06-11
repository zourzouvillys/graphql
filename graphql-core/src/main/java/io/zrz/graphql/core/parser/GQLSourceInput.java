package io.zrz.graphql.core.parser;

import org.immutables.value.Value;

@Value.Immutable(copy = true)
public abstract class GQLSourceInput {

  private static final GQLSourceInput EMPTY = ImmutableGQLSourceInput.builder().name("[none]").build();

  public abstract String name();

  public static GQLSourceInput emptySource() {
    return EMPTY;
  }

  @Override
  public String toString() {
    return this.name();
  }

  public static GQLSourceInput of(String name) {
    return ImmutableGQLSourceInput.builder().name(name).build();
  }

}
