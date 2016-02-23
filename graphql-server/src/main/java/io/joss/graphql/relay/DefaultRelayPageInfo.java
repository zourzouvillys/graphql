package io.joss.graphql.relay;

import io.joss.graphql.core.binder.annotatons.GQLField;
import io.joss.graphql.core.binder.annotatons.GQLNonNull;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class DefaultRelayPageInfo implements RelayPageInfo
{

  private boolean hasNextPage;
  private boolean hasPreviousPage;

  public static RelayPageInfo onlyPage()
  {
    return DefaultRelayPageInfo.builder().hasNextPage(false).hasPreviousPage(false).build();
  }

  public static RelayPageInfo create(boolean hasNext, boolean hasPrevious)
  {
    return DefaultRelayPageInfo.builder().hasNextPage(hasNext).hasPreviousPage(hasPrevious).build();
  }


  @GQLField
  @Override
  public @GQLNonNull Boolean hasNextPage()
  {
    return this.hasNextPage;
  }

  @GQLField
  @Override
  public @GQLNonNull Boolean hasPreviousPage()
  {
    return this.hasPreviousPage;
  }

}
