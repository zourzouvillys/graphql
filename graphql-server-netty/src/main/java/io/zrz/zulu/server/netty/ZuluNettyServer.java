package io.zrz.zulu.server.netty;

import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.zrz.graphql.zulu.engine.ZuluEngine;
import io.zrz.graphql.zulu.engine.ZuluEngineBuilder;

public class ZuluNettyServer {

  private HttpServer http;
  private ZuluEngine zulu;

  ZuluNettyServer(int port, ZuluEngine zulu) {
    this.http = new HttpServer(port, new ZuluHttpResponder(zulu));
    this.zulu = zulu;
  }

  public ZuluNettyServer startAsync() {
    http.startAsync();
    return this;
  }

  public ZuluNettyServer awaitRunning() {
    http.awaitRunning();
    return this;
  }

  public ZuluNettyServer awaitTerminated() {
    http.awaitTerminated();
    return this;
  }

  public static ZuluNettyServer create(int port, ZuluEngine engine) {
    return new ZuluNettyServer(port, engine);
  }

  public static ZuluNettyServer create(int port, Type queryRoot) {
    return new ZuluNettyServer(port, new ZuluEngine(queryRoot));
  }

  public ZuluNettyServer awaitTerminated(int timeout, TimeUnit unit) throws TimeoutException {
    http.awaitTerminated(timeout, unit);
    return this;
  }

  public static ZuluNettyServer create(int port, ZuluEngineBuilder builder) {
    return create(port, builder.build());
  }

}
