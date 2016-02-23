package io.joss.graphql.relay;

import io.joss.graphql.core.binder.annotatons.GQLNonNull;

public interface RelayConnection<T>
{

  public @GQLNonNull RelayPageInfo pageInfo();
  
  public @GQLNonNull T[] edges();

}
