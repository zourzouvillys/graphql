package io.zrz.zulu.grpc;

import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.grpc.Server;
import io.grpc.netty.NettyServerBuilder;
import io.zrz.graphql.zulu.engine.ZuluEngine;
import io.zrz.graphql.zulu.engine.ZuluEngineBuilder;
import io.zrz.zulu.graphql.GraphQLGrpc;

public class ZuluGrpcServer {

  private final ZuluEngine zulu;
  private int port;
  private Server server;

  ZuluGrpcServer(int port, ZuluEngine zulu) {
    this.port = port;
    this.zulu = zulu;
  }

  public ZuluGrpcServer startAsync() {
    this.server = NettyServerBuilder.forPort(this.port)
        // .addService(new ZuluGrpcBinder(zulu))
        .addTransportFilter(new BindingServerTransportFilter(() -> new ZuluGrpcBinder(zulu)))
        .addService(new PerSessionService(GraphQLGrpc.getServiceDescriptor()))
        .build();
    return this;
  }

  public ZuluGrpcServer awaitTerminated() throws InterruptedException {
    server.awaitTermination();
    return this;
  }

  public static ZuluGrpcServer create(int port, ZuluEngine engine) {
    return new ZuluGrpcServer(port, engine);
  }

  public static ZuluGrpcServer create(int port, Type queryRoot) {
    return new ZuluGrpcServer(port, new ZuluEngine(queryRoot));
  }

  public ZuluGrpcServer awaitTerminated(int timeout, TimeUnit unit) throws TimeoutException, InterruptedException {
    server.awaitTermination(timeout, unit);
    return this;
  }

  public static ZuluGrpcServer create(int port, ZuluEngineBuilder builder) {
    return create(port, builder.build());
  }

}
