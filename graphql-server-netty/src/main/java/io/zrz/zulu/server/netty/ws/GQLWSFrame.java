package io.zrz.zulu.server.netty.ws;

import com.fasterxml.jackson.databind.JsonNode;

public interface GQLWSFrame {

  /**
   * the frame ID
   */

  String id();

  /**
   * the type
   */

  GQLWSFrameKind type();

  /**
   * the raw underlying payload.
   */

  JsonNode rawPayload();

}
