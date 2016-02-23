package io.joss.graphql.relay;

import io.joss.graphql.core.binder.annotatons.GQLField;
import io.joss.graphql.core.binder.annotatons.GQLNonNull;

public interface RelayEdge<T>
{

  @GQLField
  public @GQLNonNull String cursor();

  @GQLField
  public T node();

}
