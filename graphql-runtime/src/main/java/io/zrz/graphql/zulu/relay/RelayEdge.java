package io.zrz.graphql.zulu.relay;

import io.zrz.graphql.zulu.annotations.GQLNullable;
import io.zrz.graphql.zulu.annotations.GQLTypeUse;

// note: doesn't export into GraphQL namespace
public interface RelayEdge<NodeT extends RelayNode> {

  @GQLNullable
  NodeT node();

  @GQLTypeUse(nullable = false)
  String cursor();

}
