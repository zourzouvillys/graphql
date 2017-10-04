package io.zrz.graphql.relay;

import io.zrz.graphql.core.binder.annotatons.GQLField;
import io.zrz.graphql.core.binder.annotatons.GQLNonNull;
import io.zrz.graphql.core.binder.annotatons.GQLType;

@GQLType(name = "PageInfo")
public abstract class RelayPageInfo
{

  @GQLField
  @GQLNonNull
  public abstract Boolean hasNextPage();

  @GQLField
  @GQLNonNull
  public abstract Boolean hasPreviousPage();

}
