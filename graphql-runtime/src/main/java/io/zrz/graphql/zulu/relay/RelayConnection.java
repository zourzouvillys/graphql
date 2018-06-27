package io.zrz.graphql.zulu.relay;

import java.util.stream.Stream;

import io.zrz.graphql.zulu.annotations.GQLField;
import io.zrz.graphql.zulu.annotations.GQLNotNull;
import io.zrz.graphql.zulu.annotations.GQLTypeUse;

// note: doesn't expose a GraphQL type directly
public interface RelayConnection<EdgeT extends RelayEdge<? extends NodeT>, NodeT extends RelayNode> {

  @GQLField
  Stream<EdgeT> edges();

  @GQLField
  default Stream<NodeT> nodes() {
    return edges().map(EdgeT::node);
  }

  @GQLTypeUse(nullable = false)
  @GQLField
  @GQLNotNull
  RelayPageInfo pageInfo();

}
