package io.zrz.zulu.server.netty;

import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.google.common.reflect.TypeToken;

import io.zrz.graphql.zulu.engine.ZuluEngine;
import io.zrz.graphql.zulu.engine.ZuluEngineBuilder;

public class ZuluNettyServer {

  private final HttpServer http;
  private final ZuluEngine zulu;
  private final ZuluHttpResponder responder;

  ZuluNettyServer(final int port, final ZuluEngine zulu) {
    this.responder = new ZuluHttpResponder(zulu);
    this.http = new HttpServer(port, this.responder);
    this.zulu = zulu;
  }

  public ZuluNettyServer startAsync() {
    this.http.startAsync();
    return this;
  }

  public ZuluNettyServer awaitRunning() {
    this.http.awaitRunning();
    return this;
  }

  public ZuluNettyServer awaitTerminated() {
    this.http.awaitTerminated();
    return this;
  }

  public static ZuluNettyServer create(final int port, final ZuluEngine engine) {
    return new ZuluNettyServer(port, engine);
  }

  public static ZuluNettyServer create(final int port, final Type queryRoot) {
    return new ZuluNettyServer(port, new ZuluEngine(queryRoot));
  }

  public ZuluNettyServer awaitTerminated(final int timeout, final TimeUnit unit) throws TimeoutException {
    this.http.awaitTerminated(timeout, unit);
    return this;
  }

  public static ZuluNettyServer create(final int port, final ZuluEngineBuilder builder) {
    return create(port, builder.build());
  }

  public <T> ZuluNettyServer bind(final Type type, final T instance) {
    this.responder.bind(TypeToken.of(type), instance);
    return this;
  }

}
