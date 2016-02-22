package io.joss.graphql.relay;

import io.joss.graphql.core.binder.annotatons.GQLField;
import io.joss.graphql.core.binder.annotatons.GQLNonNull;
import io.joss.graphql.core.binder.annotatons.GQLType;

@GQLType(name = "Node")
public interface RelayNode
{

  @GQLField(type = "ID")
  public @GQLNonNull String id();

}
