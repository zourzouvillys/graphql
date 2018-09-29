package io.zrz.zulu.server.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.zrz.zulu.server.netty.ws.GQLWSFrameDecoder;
import io.zrz.zulu.server.netty.ws.GQLWSFrameEncoder;
import io.zrz.zulu.server.netty.ws.GraphQLFrameHandler;

public class HttpServerConnector implements NettyServerConnector {

  public static final String CODEC_HANDLER_NAME = "codec_handler";
  public static final String COMPRESSOR_HANDLER_NAME = "compressor_handler";
  public static final String AGGREGATOR_HANDLER_NAME = "aggregator_handler";
  public static final String HTTP_REQUEST_HANDLER_NAME = "http_request_handler";
  private final HttpResponder responder;
  private final InMemorySessionRepository handler;

  public HttpServerConnector(final ZuluHttpEngine server, final HttpResponder responder) {
    this.responder = responder;
    this.handler = new InMemorySessionRepository(server);
  }

  @Override
  public ChannelInitializer<?> channelInitializer() {
    return new ChannelInitializer<SocketChannel>() {
      @Override
      public void initChannel(final SocketChannel ch) throws Exception {
        ch.pipeline().addLast(CODEC_HANDLER_NAME, new HttpServerCodec());
        ch.pipeline().addLast(COMPRESSOR_HANDLER_NAME, new HttpContentCompressor());
        ch.pipeline().addLast(AGGREGATOR_HANDLER_NAME, new HttpObjectAggregator(512 * 1024));

        this.initWebsockets(ch);

        ch.pipeline().addLast(HTTP_REQUEST_HANDLER_NAME, new HttpHandler(HttpServerConnector.this.responder));
      }

      private void initWebsockets(final SocketChannel ch) {
        ch.pipeline().addLast(new WebSocketServerCompressionHandler());
        ch.pipeline().addLast(new WebSocketServerProtocolHandler("/", "graphql-ws", true));
        ch.pipeline().addLast(new GQLWSFrameDecoder());
        ch.pipeline().addLast(new GQLWSFrameEncoder());
        ch.pipeline().addLast(new GraphQLFrameHandler(HttpServerConnector.this.handler));
      }

    };
  }

}
