package io.zrz.zulu.server.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

public class HttpServerConnector implements NettyServerConnector {

  public static final String CODEC_HANDLER_NAME = "codec_handler";
  public static final String COMPRESSOR_HANDLER_NAME = "compressor_handler";
  public static final String AGGREGATOR_HANDLER_NAME = "aggregator_handler";
  public static final String HTTP_REQUEST_HANDLER_NAME = "http_request_handler";
  private HttpResponder responder;

  public HttpServerConnector(HttpResponder responder) {
    this.responder = responder;
  }

  @Override
  public ChannelInitializer<?> channelInitializer() {
    return new ChannelInitializer<SocketChannel>() {
      @Override
      public void initChannel(final SocketChannel ch) throws Exception {
        ch.pipeline().addLast(CODEC_HANDLER_NAME, new HttpServerCodec());
        ch.pipeline().addLast(COMPRESSOR_HANDLER_NAME, new HttpContentCompressor());
        ch.pipeline().addLast(AGGREGATOR_HANDLER_NAME, new HttpObjectAggregator(512 * 1024));
        ch.pipeline().addLast(HTTP_REQUEST_HANDLER_NAME, new HttpHandler(responder));
      }
    };
  }

}
