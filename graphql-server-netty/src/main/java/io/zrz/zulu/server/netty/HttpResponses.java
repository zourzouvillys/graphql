package io.zrz.zulu.server.netty;

import static io.netty.buffer.Unpooled.copiedBuffer;

import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

public class HttpResponses {

  public static FullHttpResponse createSuccessResponse() {
    return createSuccessResponse(HttpResponseStatus.OK.reasonPhrase());
  }

  public static FullHttpResponse createSuccessResponse(final String payload) {
    return createResponse(HttpResponseStatus.OK, payload);
  }

  public static FullHttpResponse createResponse(final HttpResponseStatus status) {
    return createResponse(status, status.reasonPhrase());
  }

  public static FullHttpResponse createResponse(final HttpResponseStatus status, final String payload) {
    return createResponse(status, payload.getBytes());
  }

  public static FullHttpResponse createResponse(final HttpResponseStatus status, final byte[] payload) {
    return createResponse(status, payload, HttpVersion.HTTP_1_1);
  }

  public static FullHttpResponse createResponse(final HttpResponseStatus status, final byte[] payload, final HttpVersion version) {
    if (payload.length > 0) {
      final FullHttpResponse response = new DefaultFullHttpResponse(version, status, copiedBuffer(payload));
      response.headers().set(HttpHeaders.Names.CONTENT_LENGTH, payload.length);
      return response;
    }
    else {
      return new DefaultFullHttpResponse(version, status);
    }
  }

}
