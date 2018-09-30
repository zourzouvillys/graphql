package io.zrz.zulu.server.netty.ws;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.zrz.graphql.zulu.engine.ZuluWarning;

public class SimpleGQLWSFrame implements GQLWSFrame {

  private final String frameId;
  private final GQLWSFrameKind type;
  private final JsonNode payload;

  SimpleGQLWSFrame(final String frameId, final GQLWSFrameKind type, final JsonNode payload) {
    this.frameId = frameId;
    this.type = type;
    this.payload = payload;
  }

  @Override
  public String id() {
    return this.frameId;
  }

  @Override
  public GQLWSFrameKind type() {
    return this.type;
  }

  @Override
  public JsonNode rawPayload() {
    return this.payload;
  }

  @Override
  public String toString() {
    return this.id() + ": " + this.type().kindName() + " " + this.rawPayload();
  }

  public static SimpleGQLWSFrame ack() {
    return new SimpleGQLWSFrame(null, StandardGQLWSFrameKind.GQL_CONNECTION_ACK, null);
  }

  public static SimpleGQLWSFrame keepAlive() {
    return new SimpleGQLWSFrame(null, StandardGQLWSFrameKind.GQL_CONNECTION_KEEP_ALIVE, null);
  }

  public static SimpleGQLWSFrame data(final String id, final ObjectNode content) {
    return new SimpleGQLWSFrame(id, StandardGQLWSFrameKind.GQL_DATA, content);
  }

  public static SimpleGQLWSFrame data(final String id, final ObjectNode data, final List<ZuluWarning> errors, final ObjectNode extensions) {
    final ObjectNode content = JsonNodeFactory.instance.objectNode();
    content.set("data", data);
    if ((errors != null) && !errors.isEmpty()) {
      // TODO: really need a better way of handling error encoding.
      final ArrayNode errarr = content.withArray("errors");
      for (final ZuluWarning err : errors) {
        final ObjectNode erritem = errarr.addObject();
        erritem.put("type", err.warningKind().name());
        erritem.put("message", err.detail());
      }
    }
    if (extensions != null) {
      content.set("extensions", extensions);
    }
    return data(id, content);
  }

  public static GQLWSFrame complete(final String id) {
    return new SimpleGQLWSFrame(id, StandardGQLWSFrameKind.GQL_COMPLETE, null);
  }

  public static GQLWSFrame error(final String id, final Throwable err) {
    return new SimpleGQLWSFrame(id, StandardGQLWSFrameKind.GQL_ERROR, null);
  }

}
