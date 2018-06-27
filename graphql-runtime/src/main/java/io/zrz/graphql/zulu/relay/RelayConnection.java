package io.zrz.graphql.zulu.relay;

import java.util.stream.Stream;

import io.zrz.graphql.zulu.annotations.GQLField;
import io.zrz.graphql.zulu.annotations.GQLNotNull;

// note: doesn't expose a GraphQL type directly
public interface RelayConnection<EdgeT extends RelayEdge<? extends NodeT>, NodeT extends RelayNode> {

  @GQLField
  Stream<@GQLNotNull EdgeT> edges();

  @GQLField
  default Stream<@GQLNotNull NodeT> nodes() {
    return edges().map(EdgeT::node);
  }

  @GQLField
  @GQLNotNull
  RelayPageInfo pageInfo();

}
