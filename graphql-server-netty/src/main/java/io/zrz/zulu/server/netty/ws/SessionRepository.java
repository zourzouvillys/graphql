package io.zrz.zulu.server.netty.ws;

import com.fasterxml.jackson.databind.JsonNode;

import io.reactivex.Flowable;

public interface SessionRepository {

  GQLWebSocketSession initializeSession(JsonNode rawPayload, Flowable<GQLWSFrame> input);

}
