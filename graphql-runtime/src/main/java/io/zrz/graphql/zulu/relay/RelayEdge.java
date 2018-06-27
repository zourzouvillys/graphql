package io.zrz.graphql.zulu.relay;

import io.zrz.graphql.zulu.annotations.GQLNotNull;
import io.zrz.graphql.zulu.annotations.GQLNullable;

// doesn't export into GraphQL namespace
public interface RelayEdge<NodeT extends RelayNode> {

  @GQLNullable
  NodeT node();

  @GQLNotNull
  String cursor();

}
