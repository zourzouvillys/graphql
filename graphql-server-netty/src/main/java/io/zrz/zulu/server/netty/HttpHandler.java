package io.zrz.zulu.server.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;

public class HttpHandler extends ChannelInboundHandlerAdapter {

  private final HttpResponder responder;

  public HttpHandler(final HttpResponder responder) {
    this.responder = responder;
  }

  @Override
  public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception {

    if (msg instanceof FullHttpRequest) {
      ctx.writeAndFlush(responder.processRequest((FullHttpRequest) msg));
    }
    else {
      super.channelRead(ctx, msg);
    }

  }

  @Override
  public void channelReadComplete(final ChannelHandlerContext ctx) throws Exception {
    ctx.flush();
  }

  @Override
  public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) throws Exception {

    cause.printStackTrace();

    // ctx.writeAndFlush(HttpResponder.createResponse(HttpResponseStatus.INTERNAL_SERVER_ERROR, cause.getMessage()));
    // log.error("Channel exception caught", cause);

  }

}
