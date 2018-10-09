package io.zrz.zulu.server.netty;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.reflect.TypeToken;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http2.DefaultHttp2DataFrame;
import io.netty.handler.codec.http2.DefaultHttp2Headers;
import io.netty.handler.codec.http2.DefaultHttp2HeadersFrame;
import io.netty.handler.codec.http2.Http2StreamFrame;
import io.reactivex.Flowable;
import io.reactivex.functions.Predicate;
import io.zrz.graphql.zulu.engine.ZuluEngine;
import io.zrz.graphql.zulu.engine.ZuluEngineBuilder;
import io.zrz.graphql.zulu.engine.ZuluExecutionScopeProvider;
import io.zrz.graphql.zulu.engine.ZuluWarning;
import io.zrz.zulu.server.netty.ws.GQLWSFrame;
import io.zrz.zulu.server.netty.ws.GQLWSFrames;
import io.zrz.zulu.server.netty.ws.GQLWSRequest;
import io.zrz.zulu.server.netty.ws.SimpleGQLWSFrame;
import io.zrz.zulu.server.netty.ws.StandardGQLWSFrameKind;
import zrz.webports.spi.IncomingWebSocket;

public class ZuluNettyServer {

  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ZuluNettyServer.class);

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

  public ZuluHttpEngine engine() {
    return this.engine;
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

  /**
   * provides a handler that
   *
   * @param req
   * @return
   */

  public Flowable<WebSocketFrame> websocket(final IncomingWebSocket req) {

    return req
        .incoming()
        .cast(TextWebSocketFrame.class)
        .map(TextWebSocketFrame::text)
        .map(GQLWSFrames::decode)
        .doOnNext(msg -> {

          if (msg.id() != null) {
            return;
          }

          //
          switch (msg.type().kindName()) {
            case "connection_init":
              log.debug("initialized connection, {}", msg.rawPayload());
              break;
            default:
              log.info("unknown graphql-ws frame: {}", msg);
              break;
          }

        })

        .doAfterTerminate(() -> log.info("completed session"))

        .filter(f -> f.id() != null)

        .groupBy(f -> f.id())

        .flatMap(flow -> {

          return flow
              .takeUntil((Predicate<GQLWSFrame>) f -> f.type().kindName().equals("stop"))
              .onErrorReturn(err -> SimpleGQLWSFrame.error(flow.getKey(), err))
              .filter(f -> f.type() == StandardGQLWSFrameKind.GQL_START)
              .flatMap(e -> {

                log.info("subscribing {}", e);

                final Flowable<ZuluJacksonResult> res = this.engine.execute(new GQLWSRequest(e));

                // apollo-link-ws errors if we send a null/missing "data" field for a subscription, so
                // there is no way to indicate we accepted it.

                return res; // .startWith(Flowable.just(new EmptyZuluJacksonResult()));

              })
              .map(data -> {

                final ObjectNode content = data.data();
                final List<ZuluWarning> errors = data.errors();
                log.debug("sending frame {}", data, errors);
                return SimpleGQLWSFrame.data(flow.getKey(), content, errors, data.extensions());

              })
              .doAfterTerminate(() -> log.info("query completed"));

        })
        .map(GQLWSFrames::encode)
        .map(TextWebSocketFrame::new);

  }

  public Flowable<Http2StreamFrame> process(final IncomingZuluRequest req) {

    if (req.isOptions()) {
      log.debug("CORS preflight/OPTIONS request, origin {}", req.origin());
      final DefaultHttp2Headers headers = new DefaultHttp2Headers();
      headers.status(HttpResponseStatus.OK.codeAsText());
      // Access-Control-Request-Headers
      // Access-Control-Request-Method
      req.origin().ifPresent(origin -> this.addCORS(origin, headers));
      this.addHeaders(headers);
      return Flowable.just(new DefaultHttp2HeadersFrame(headers, true));
    }

    // a full request.

    final HttpOperationRequest opreq = req.request();

    log.trace("GraphQL request {}", req);

    return this.engine
        .execute(opreq)
        .singleOrError()
        .toFlowable()
        //
        // .flatMap(res -> {
        //
        // final Flowable<ZuluJacksonResult> dataset = this.engine.execute(null);
        //
        // // apollo-link-ws errors if we send a null/missing "data" field for a subscription, so
        // // there is no way to indicate we accepted it.
        //
        // return dataset; // .startWith(Flowable.just(new EmptyZuluJacksonResult()));
        //
        // })
        .flatMap(data -> {

          log.debug("response {}", data);

          final DefaultHttp2Headers headers = new DefaultHttp2Headers(true);

          headers.status(HttpResponseStatus.OK.codeAsText());

          req.origin().ifPresent(origin -> this.addCORS(origin, headers));

          this.addHeaders(headers);

          final DefaultHttp2DataFrame body = new DefaultHttp2DataFrame(ZuluNettyUtils.toByteBuf(data), true);

          return Flowable.<Http2StreamFrame>just(new DefaultHttp2HeadersFrame(headers), body);

          // final ObjectNode content = data.data();
          // final List<ZuluWarning> errors = data.errors();
          // log.debug("sending frame {}", data, errors);
          // return SimpleGQLWSFrame.data(flow.getKey(), content, errors, data.extensions());
          // return null;

        })
        .doAfterTerminate(() -> log.info("query completed"));

  }

  private void addHeaders(final DefaultHttp2Headers headers) {
    headers.add("feature-policy", "sync-xhr 'none'; document-write 'none'");
    headers.add("content-security-policy", "default-src 'none';");
    headers.add("referrer-policy", "strict-origin");
    headers.add(HttpHeaderNames.X_FRAME_OPTIONS, "SAMEORIGIN");
    headers.add("strict-transport-security", "max-age=31536000000; includeSubDomains");
    headers.add("x-xss-protection", "1; mode=block");
    headers.add("x-content-type-options", "nosniff");
  }

  private void addCORS(final CharSequence origin, final DefaultHttp2Headers headers) {
    headers.add(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, origin);
    headers.add(HttpHeaderNames.ACCESS_CONTROL_EXPOSE_HEADERS, "content-type,etag,vary,content-encoding,authorization");
    headers.addBoolean(HttpHeaderNames.ACCESS_CONTROL_ALLOW_CREDENTIALS, true);
    headers.add(HttpHeaderNames.ACCESS_CONTROL_ALLOW_METHODS, "POST,GET,OPTIONS");
    headers.add(HttpHeaderNames.ACCESS_CONTROL_ALLOW_HEADERS, "content-type,accept,if-none-match,authorization");
    headers.addInt(HttpHeaderNames.ACCESS_CONTROL_MAX_AGE, 600);
  }

}
