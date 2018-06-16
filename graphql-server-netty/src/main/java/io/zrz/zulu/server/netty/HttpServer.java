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

  private List<ChannelFuture> channels = Lists.newArrayList();

  private NettyServerConnector connector;

  public HttpServer(HttpResponder responder) {
    this.connector = new HttpServerConnector(responder);
  }

  @Override
  protected void doStart() {

    final ServerBootstrap bootstrap = new ServerBootstrap()
        .group(masterGroup, slaveGroup)
        .channel(NioServerSocketChannel.class)
        .childHandler(connector.channelInitializer())
        .option(ChannelOption.SO_BACKLOG, 128)
        .childOption(ChannelOption.SO_KEEPALIVE, true);

    try {
      channels.add(bootstrap.bind(8888).sync());
      super.notifyStarted();
    }
    catch (final InterruptedException e) {
      super.notifyFailed(e);
    }

  }

  @Override
  protected void doStop() {

    slaveGroup.shutdownGracefully();
    masterGroup.shutdownGracefully();

    for (final ChannelFuture channel : channels) {
      try {
        channel.channel().closeFuture().sync();
      }
      catch (final InterruptedException e) {
      }
    }

    notifyStopped();

  }

}
