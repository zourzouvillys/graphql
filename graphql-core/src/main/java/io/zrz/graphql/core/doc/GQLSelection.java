package io.zrz.graphql.core.doc;

import io.zrz.graphql.core.parser.GQLSourceLocation;

public interface GQLSelection {

  <R> R apply(GQLSelectionVisitor<R> visitor);

  GQLSourceLocation location();

}
