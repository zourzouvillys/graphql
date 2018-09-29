package io.zrz.zulu.server.netty.ws;

import java.util.Optional;

public enum StandardGQLWSFrameKind implements GQLWSFrameKind {

  // client -> server

  GQL_CONNECTION_INIT,

  GQL_START,

  GQL_STOP,

  GQL_CONNECTION_TERMINATE,

  // server -> client

  GQL_CONNECTION_ERROR,

  GQL_CONNECTION_ACK,

  GQL_DATA,

  GQL_ERROR,

  GQL_COMPLETE,

  GQL_CONNECTION_KEEP_ALIVE,

  ;

  @Override
  public String kindName() {
    return this.name().substring(4).toLowerCase();
  }

  public static Optional<StandardGQLWSFrameKind> fromProtocol(final String type) {
    final String name = "GQL_" + type.toUpperCase();
    return Optional.ofNullable(valueOf(name));
  }

}
