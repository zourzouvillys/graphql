package io.zrz.zulu.server.netty;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Preconditions;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;
import io.zrz.graphql.zulu.engine.ZuluWarning;

public class ZuluNettyUtils {

  public static ObjectNode toFullResponse(final ObjectNode data, final List<ZuluWarning> errors, final ObjectNode extensions) {
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
    return content;
  }

  private static final ObjectMapper objectMapper = new ObjectMapper();

  public static ByteBuf toByteBuf(final ObjectNode res) {
    try {
      return Unpooled.wrappedBuffer(objectMapper.writeValueAsBytes(res));
    }
    catch (final JsonProcessingException e) {
      // TODO Auto-generated catch block
      throw new RuntimeException(e);
    }
  }

  public static ByteBuf toByteBuf(final ZuluJacksonResult res) {
    final ObjectNode data = res.data();
    return toByteBuf(toFullResponse(data, res.errors(), res.extensions()));
  }

  public static JsonNode fromByteBuf(final ByteBuf buf, final boolean release) {

    Preconditions.checkArgument(buf.isReadable());

    try (ByteBufInputStream in = new ByteBufInputStream(buf, release)) {

      return requireNonNull(objectMapper.readTree(in));

    }
    catch (final IOException e) {
      throw new RuntimeException(e);
    }

  }

}
