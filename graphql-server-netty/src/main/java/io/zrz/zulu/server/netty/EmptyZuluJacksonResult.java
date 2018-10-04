package io.zrz.zulu.server.netty;

import java.util.List;

import com.fasterxml.jackson.databind.node.ObjectNode;

import io.zrz.graphql.zulu.engine.ZuluWarning;

public class EmptyZuluJacksonResult implements ZuluJacksonResult {

  @Override
  public ObjectNode data() {
    return null;
  }

  @Override
  public List<ZuluWarning> errors() {
    return null;
  }

  @Override
  public ObjectNode extensions() {
    return null;
  }

}
