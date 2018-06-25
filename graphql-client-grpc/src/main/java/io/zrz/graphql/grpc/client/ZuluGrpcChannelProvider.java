package io.zrz.graphql.grpc.client;

import io.grpc.ManagedChannel;
import io.grpc.inprocess.InProcessChannelBuilder;

public class ZuluGrpcChannelProvider {

  public ZuluGrpcChannel get() {
    final ManagedChannel ch = InProcessChannelBuilder.forName("zulu").build();
    return new ZuluGrpcChannel(ch);
  }

}
