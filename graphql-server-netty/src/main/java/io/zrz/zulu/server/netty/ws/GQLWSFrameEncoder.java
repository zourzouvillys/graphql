package io.zrz.zulu.server.netty.ws;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

public class GQLWSFrameEncoder extends MessageToMessageEncoder<GQLWSFrame> {

  private static ObjectMapper mapper = new ObjectMapper();

  @Override
  protected void encode(final ChannelHandlerContext ctx, final GQLWSFrame msg, final List<Object> out) throws Exception {

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

    out.add(new TextWebSocketFrame(mapper.writeValueAsString(node)));

  }

}
