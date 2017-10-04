package io.zrz.graphql.relay;

import io.zrz.graphql.core.binder.annotatons.GQLField;
import io.zrz.graphql.core.binder.annotatons.GQLNonNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = false)
@Builder
public class DefaultRelayPageInfo extends RelayPageInfo
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
