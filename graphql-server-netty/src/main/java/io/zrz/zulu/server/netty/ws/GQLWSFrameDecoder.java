package io.zrz.zulu.server.netty.ws;

import java.util.List;
import java.util.Optional;

import org.eclipse.jdt.annotation.NonNull;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

public class GQLWSFrameDecoder extends MessageToMessageDecoder<TextWebSocketFrame> {

  private static ObjectMapper mapper = new ObjectMapper();

  public static class OperationMessage {
    public JsonNode payload;
    public String id;
    public String type;
  }

  @Override
  protected void decode(final ChannelHandlerContext ctx, final TextWebSocketFrame msg, final List<Object> out) throws Exception {
    out.add(this.handle(ctx, mapper.readValue(msg.text(), OperationMessage.class)));
  }

  GQLWSFrameKind toKind(final String value) {

    final Optional<StandardGQLWSFrameKind> type = StandardGQLWSFrameKind.fromProtocol(value);

    if (type.isPresent()) {
      return type.get();
    }

    return new UnknownGQLWSFrameKind(value);

  }

  GQLWSFrame handle(final ChannelHandlerContext ctx, @NonNull final OperationMessage msg) {
    return new SimpleGQLWSFrame(msg.id, this.toKind(msg.type), msg.payload);
  }

}
