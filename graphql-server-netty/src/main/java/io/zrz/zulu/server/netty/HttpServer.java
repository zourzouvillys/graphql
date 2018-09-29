package io.zrz.zulu.server.netty;

import java.util.List;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.AbstractService;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class HttpServer extends AbstractService {

  private final EventLoopGroup masterGroup = new NioEventLoopGroup();
  private final EventLoopGroup slaveGroup = new NioEventLoopGroup();

  private final List<ChannelFuture> channels = Lists.newArrayList();

  private final NettyServerConnector connector;
  private final int port;

  public HttpServer(final ZuluHttpEngine server, final int port, final HttpResponder responder) {
    this.port = port;
    this.connector = new HttpServerConnector(server, responder);
  }

  @Override
  protected void doStart() {

    final ServerBootstrap bootstrap = new ServerBootstrap()
        .group(this.masterGroup, this.slaveGroup)
        .channel(NioServerSocketChannel.class)
        .childHandler(this.connector.channelInitializer())
        .option(ChannelOption.SO_BACKLOG, 128)
        .childOption(ChannelOption.TCP_NODELAY, true)
        .childOption(ChannelOption.SO_KEEPALIVE, true);

    try {
      this.channels.add(bootstrap.bind(this.port).sync());
      super.notifyStarted();
    }
    catch (final InterruptedException e) {
      super.notifyFailed(e);
    }

  }

  @Override
  protected void doStop() {

    this.slaveGroup.shutdownGracefully();
    this.masterGroup.shutdownGracefully();

    for (final ChannelFuture channel : this.channels) {
      try {
        channel.channel().closeFuture().sync();
      }
      catch (final InterruptedException e) {
      }
    }

    this.notifyStopped();

  }

}
