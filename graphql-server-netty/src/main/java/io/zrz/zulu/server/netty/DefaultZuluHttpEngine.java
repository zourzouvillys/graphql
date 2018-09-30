package io.zrz.zulu.server.netty;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.node.ObjectNode;

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
          log.debug("subscription cancelled");
          portal.cancel();
        })
        .map(this::mapResult);

  }

  ZuluJacksonResult mapResult(final ZuluResult res) {

    final ZuluDataResult data = (ZuluDataResult) res;

    return new ZuluJacksonResult() {

      @Override
      public ObjectNode extensions() {
        return null;
      }

      @Override
      public List<ZuluWarning> errors() {
        return data.errors();
      }

      @Override
      public ObjectNode data() {
        final ZuluDataResult data = (ZuluDataResult) res;
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (JsonGenerator jg = DefaultZuluHttpEngine.this.responder.mapper().getFactory().createGenerator(baos)) {
          jg.writeStartObject();
          final JacksonResultReceiver receiver = new JacksonResultReceiver(jg);
          data.data(receiver);
          jg.writeEndObject();
          jg.flush();
          jg.close();
          return DefaultZuluHttpEngine.this.responder.mapper().readValue(baos.toByteArray(), ObjectNode.class);
        }
        catch (final IOException e) {
          throw new RuntimeException(e);
        }
      }

    };

  }

}
