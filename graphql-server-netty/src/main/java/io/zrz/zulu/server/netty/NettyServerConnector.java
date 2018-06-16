package io.zrz.zulu.server.netty;

import io.netty.channel.ChannelInitializer;

public interface NettyServerConnector {

  ChannelInitializer<?> channelInitializer();

}
