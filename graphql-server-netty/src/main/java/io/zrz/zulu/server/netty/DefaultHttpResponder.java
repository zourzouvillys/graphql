package io.zrz.zulu.server.netty;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

public class DefaultHttpResponder implements HttpResponder {

  @Override
  public FullHttpResponse processRequest(final FullHttpRequest request) {
    return HttpResponses.createSuccessResponse("Hello there");
  }

}
