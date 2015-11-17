package io.joss.graphql.core.doc;

public interface GQLSelection
{

  <R> R apply(GQLSelectionVisitor<R> visitor);
  
}
