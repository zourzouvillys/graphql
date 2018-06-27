package io.zrz.graphql.zulu.relay;

import io.zrz.graphql.zulu.annotations.GQLInterfaceType;
import io.zrz.graphql.zulu.annotations.GQLTypeUse;

@GQLInterfaceType(name = "Node")
public interface RelayNode {

  @GQLTypeUse(name = "ID", nullable = false)
  String id();

}
