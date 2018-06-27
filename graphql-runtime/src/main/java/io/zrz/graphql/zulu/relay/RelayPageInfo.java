package io.zrz.graphql.zulu.relay;

import io.zrz.graphql.zulu.annotations.GQLObjectType;

@GQLObjectType(name = "PageInfo")
public interface RelayPageInfo {

  boolean hasNextPage();

  boolean hasPreviousPage();

}
