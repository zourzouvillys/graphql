package io.zrz.graphql.relay;

import io.zrz.graphql.core.binder.annotatons.GQLField;
import io.zrz.graphql.core.binder.annotatons.GQLNonNull;
import io.zrz.graphql.core.binder.annotatons.GQLType;

@GQLType(name = "Node")
public interface RelayNode
{

  @GQLField(type = "ID")
  public @GQLNonNull String id();

}
