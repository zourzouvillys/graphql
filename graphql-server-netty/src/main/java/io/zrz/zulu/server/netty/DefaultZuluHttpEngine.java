package io.zrz.zulu.server.netty;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableList;

import hu.akarnokd.rxjava2.interop.FlowInterop;
import io.reactivex.Flowable;
import io.zrz.graphql.plugins.jackson.JacksonResultReceiver;
import io.zrz.graphql.plugins.jackson.ZuluJacksonParameterProvider;
import io.zrz.graphql.zulu.engine.ZuluEngine;
import io.zrz.graphql.zulu.engine.ZuluPortal;
import io.zrz.graphql.zulu.engine.ZuluWarning;
import io.zrz.graphql.zulu.server.ImmutableBindParams;
import zulu.runtime.subscriptions.ZuluDataResult;
import zulu.runtime.subscriptions.ZuluResult;

/**
 * provides common functionality shared between the HTTP endpoint and websocket.
 *
 * @author theo
 *
 */

public class DefaultZuluHttpEngine implements ZuluHttpEngine {

  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(DefaultZuluHttpEngine.class);
  private final ZuluEngine zulu;
  private final ZuluHttpResponder responder;

  public DefaultZuluHttpEngine(final ZuluEngine zulu, final ZuluHttpResponder responder) {
    this.zulu = zulu;
    this.responder = responder;
  }

  @Override
  public Flowable<ZuluJacksonResult> execute(final HttpOperationRequest req) {

    final ImmutableBindParams query = ImmutableBindParams.builder()
        .query(req.query())
        .operationName(req.operationName())
        .variables(new ZuluJacksonParameterProvider(this.responder.mapper(), req.variables()))
        .build();

    final ZuluPortal portal = this.zulu.bind(query, this.responder);

    return FlowInterop
        .fromFlowPublisher(portal)
        .doOnCancel(() -> {
          log.debug("query cancelled");
          portal.cancel();
        })
        .map(this::mapResult);

  }

  ZuluJacksonResult mapResult(final ZuluResult res) {

    final ZuluDataResult data = (ZuluDataResult) res;
    final ObjectNode json;
    final List<ZuluWarning> notes = new LinkedList<>();

    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try (JsonGenerator jg = DefaultZuluHttpEngine.this.responder.mapper().getFactory().createGenerator(baos)) {
      final JacksonResultReceiver receiver = new JacksonResultReceiver(jg);
      data.data(receiver, note -> notes.add(note));
      jg.flush();
      jg.close();
      final byte[] bytes = baos.toByteArray();
      if (!notes.isEmpty()) {
        log.warn("notes: {}", notes);
      }
      if (bytes.length == 0) {
        log.warn("null result");
        json = JsonNodeFactory.instance.objectNode();
      }
      else {
        json = DefaultZuluHttpEngine.this.responder.mapper().readValue(bytes, ObjectNode.class);
      }
    }
    catch (final IOException e) {
      throw new RuntimeException(e);
    }

    return new ZuluJacksonResult() {

      @Override
      public ObjectNode extensions() {
        return null;
      }

      @Override
      public List<ZuluWarning> errors() {
        return ImmutableList.<ZuluWarning>builder().addAll(data.errors()).addAll(notes).build();
      }

      @Override
      public ObjectNode data() {
        return json;
      }

      @Override
      public String toString() {
        return "mappedResult(" + data + "): " + this.errors();
      }

    };

  }

}
