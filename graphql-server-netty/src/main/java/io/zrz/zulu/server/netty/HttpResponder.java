package io.zrz.zulu.server.netty;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

public interface HttpResponder {

  public FullHttpResponse processRequest(final FullHttpRequest request);

}
