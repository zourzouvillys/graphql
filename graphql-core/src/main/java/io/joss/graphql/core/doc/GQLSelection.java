package io.joss.graphql.core.doc;

import io.joss.graphql.core.parser.GQLSourceLocation;

public interface GQLSelection {

  <R> R apply(GQLSelectionVisitor<R> visitor);

  GQLSourceLocation location();

}
