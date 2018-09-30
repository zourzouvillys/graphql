package io.zrz.zulu.server.netty.ws;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.reactivestreams.Subscriber;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
import io.reactivex.processors.UnicastProcessor;
import io.zrz.graphql.zulu.engine.ZuluWarning;
import io.zrz.zulu.server.netty.ZuluHttpEngine;

/**
 * a logical web socket context.
 *
 * @author theo
 *
 */

public class GQLWebSocketSession extends Flowable<GQLWSFrame> {

  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(GQLWebSocketSession.class);
  private static ObjectMapper mapper = new ObjectMapper();
  private final UnicastProcessor<GQLWSFrame> writer;
  private final JsonNode initPayload;
  private final ZuluHttpEngine engine;

  public GQLWebSocketSession(final ZuluHttpEngine engine, final JsonNode initPayload, final Flowable<GQLWSFrame> input) {
    this.initPayload = initPayload;
    this.writer = UnicastProcessor.create();
    this.engine = engine;
  }

  public void process(final GQLWSFrame msg) {
    switch (msg.type().kindName()) {
      case "connection_init":
        log.warn("can't reinitialize on existing session");
        break;
      case "start":
        this.processStart(msg);
        break;
      case "stop":
        this.processStop(msg);
        break;
      default:
        log.info("unknown graphql-ws frame: {}", msg);
        break;
    }
  }

  private void processStop(final GQLWSFrame msg) {

    final Disposable h = this.handles.get(msg.id());

    if (h != null) {
      h.dispose();
    }

  }

  private final Map<String, Disposable> handles = new HashMap<>();

  /**
   * pass request to the engine to execute, and dispatch the responses to the socket.
   */

  private void processStart(final GQLWSFrame start) {
    final String id = start.id();
    log.debug("starting query[{}]: {}", start.id(), start.rawPayload());
    final Disposable handle = this.engine.execute(new GQLWSRequest(this, start))
        .subscribe(
            data -> {
              final ObjectNode content = data.data();
              final List<ZuluWarning> errors = data.errors();
              log.trace("sending frame {}", data, errors);
              this.writer.onNext(SimpleGQLWSFrame.data(start.id(), content, errors, data.extensions()));
            },
            err -> {
              log.info("error processing WS operation", err);
              this.handles.remove(id);
              this.writer.onNext(SimpleGQLWSFrame.error(start.id(), err));
            },
            () -> {
              log.trace("completed operation");
              this.handles.remove(id);
              this.writer.onNext(SimpleGQLWSFrame.complete(start.id()));
            });

    this.handles.put(id, handle);

  }

  @Override
  protected void subscribeActual(final Subscriber<? super GQLWSFrame> s) {
    this.writer.subscribe(s);
  }

}
