package io.zrz.zulu.server.netty;

import com.fasterxml.jackson.databind.JsonNode;

import io.reactivex.Flowable;
import io.zrz.zulu.server.netty.ws.GQLWSFrame;
import io.zrz.zulu.server.netty.ws.GQLWebSocketSession;
import io.zrz.zulu.server.netty.ws.SessionRepository;

public class InMemorySessionRepository implements SessionRepository {

  private final ZuluHttpEngine engine;

  public InMemorySessionRepository(final ZuluHttpEngine engine) {
    this.engine = engine;
  }

  @Override
  public GQLWebSocketSession initializeSession(final JsonNode rawPayload, final Flowable<GQLWSFrame> input) {
    return new GQLWebSocketSession(this.engine, rawPayload, input);
  }

}
