package io.zrz.zulu.server.netty;

import com.fasterxml.jackson.databind.node.ObjectNode;

public interface HttpOperationRequest {

  String operationName();

  String query();

  ObjectNode variables();

  ObjectNode extensions();

  boolean isSchemaRequest();

}
