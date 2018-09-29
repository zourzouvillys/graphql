package io.zrz.zulu.server.netty;

import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.reflect.TypeToken;

import io.zrz.graphql.zulu.engine.ZuluEngine;
import io.zrz.graphql.zulu.engine.ZuluEngineBuilder;
import io.zrz.graphql.zulu.engine.ZuluExecutionScopeProvider;

public class ZuluNettyServer {

  private final HttpServer http;
  private final ZuluEngine zulu;
  private final ZuluHttpResponder responder;
  private final DefaultZuluHttpEngine engine;

  ZuluNettyServer(final int port, final ZuluEngine zulu) {
    this.responder = new ZuluHttpResponder(zulu, new ObjectMapper());
    this.engine = new DefaultZuluHttpEngine(zulu, this.responder);
    this.http = new HttpServer(this.engine, port, this.responder);
    this.zulu = zulu;
  }

  ZuluNettyServer(final int port, final ZuluEngine zulu, final ObjectMapper mapper) {
    this.responder = new ZuluHttpResponder(zulu, mapper);
    this.engine = new DefaultZuluHttpEngine(zulu, this.responder);
    this.http = new HttpServer(this.engine, port, this.responder);
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

  public static ZuluNettyServer create(final int port, final ZuluEngine engine, final ObjectMapper mapper) {
    return new ZuluNettyServer(port, engine, mapper);
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

  public <T> ZuluNettyServer contextProvider(final Type type, final ZuluExecutionScopeProvider<?> provider) {
    this.responder.contextProvider(type, provider);
    return this;
  }

  public <T> ZuluNettyServer exceptionMapper(final Type exceptionType, final Function<Throwable, ObjectNode> mapper) {
    this.responder.exceptionMappers.put(exceptionType, mapper);
    return this;
  }

}
