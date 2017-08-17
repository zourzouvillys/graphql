package io.joss.graphql.core.parser;

import lombok.Value;

@Value
public class GQLSourceInput {

  private static final GQLSourceInput EMPTY = new GQLSourceInput("[none]");

  private String name;

  public static GQLSourceInput emptySource() {
    return EMPTY;
  }

  @Override
  public String toString() {
    return this.name;
  }

}
