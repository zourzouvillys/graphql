package io.zrz.zulu.server.netty.ws;

import java.io.IOException;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public final class GQLWSFrames {

  private static ObjectMapper mapper = new ObjectMapper();

  public static GQLWSFrame decode(final String json) {
    try {
      final OperationMessage msg = mapper.readValue(json, OperationMessage.class);
      return new SimpleGQLWSFrame(msg.id, toKind(msg.type), msg.payload);
    }
    catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static String encode(final GQLWSFrame msg) {

    final ObjectNode node = mapper.createObjectNode();

    if (msg.id() != null) {
      node.put("id", msg.id());
    }

    if (msg.type() != null) {
      node.put("type", msg.type().kindName());
    }

    if (msg.rawPayload() != null) {
      node.set("payload", msg.rawPayload());
    }

    try {
      return mapper.writeValueAsString(node);
    }
    catch (final JsonProcessingException e) {
      throw new RuntimeException(e);
    }

  }

  private static class OperationMessage {
    public JsonNode payload;
    public String id;
    public String type;
  }

  public static GQLWSFrameKind toKind(final String value) {

    final Optional<StandardGQLWSFrameKind> type = StandardGQLWSFrameKind.fromProtocol(value);

    if (type.isPresent()) {
      return type.get();
    }

    return new UnknownGQLWSFrameKind(value);

  }

}
