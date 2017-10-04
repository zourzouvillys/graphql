package io.zrz.graphql.relay;

import io.zrz.graphql.core.binder.annotatons.GQLField;
import io.zrz.graphql.core.binder.annotatons.GQLNonNull;

public interface RelayEdge<T>
{

  @GQLField
  public @GQLNonNull String cursor();

  @GQLField
  public T node();

}
