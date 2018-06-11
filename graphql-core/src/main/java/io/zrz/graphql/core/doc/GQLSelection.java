package io.zrz.graphql.core.doc;

import org.eclipse.jdt.annotation.Nullable;

import io.zrz.graphql.core.parser.GQLSourceLocation;

public interface GQLSelection {

  <R> R apply(GQLSelectionVisitor<R> visitor);

  @Nullable
  GQLSourceLocation location();

  GQLSelection withLocation(GQLSourceLocation location);

}
