package io.joss.graphql.relay;

import io.joss.graphql.core.binder.annotatons.GQLField;
import io.joss.graphql.core.binder.annotatons.GQLNonNull;
import io.joss.graphql.core.binder.annotatons.GQLType;

@GQLType(name = "PageInfo")
public interface RelayPageInfo
{

  @GQLField
  @GQLNonNull
  Boolean hasNextPage();

  @GQLField
  @GQLNonNull
  Boolean hasPreviousPage();

}
