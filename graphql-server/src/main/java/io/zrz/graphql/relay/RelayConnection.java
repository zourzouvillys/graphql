package io.zrz.graphql.relay;

import io.zrz.graphql.core.binder.annotatons.GQLNonNull;

public interface RelayConnection<T>
{

  public @GQLNonNull RelayPageInfo pageInfo();
  
  public @GQLNonNull T[] edges();

}
