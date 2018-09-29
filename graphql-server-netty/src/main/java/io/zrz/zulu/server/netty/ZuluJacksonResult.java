package io.zrz.zulu.server.netty;

import java.util.List;

import com.fasterxml.jackson.databind.node.ObjectNode;

import io.zrz.graphql.zulu.engine.ZuluWarning;

public interface ZuluJacksonResult {

  ObjectNode data();

  List<ZuluWarning> errors();

  ObjectNode extensions();

}
