package io.zrz.zulu.server.netty.ws;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.zrz.zulu.server.netty.HttpOperationRequest;

public class GQLWSRequest implements HttpOperationRequest {

  private final JsonNode payload;

  public GQLWSRequest(final GQLWSFrame start) {
    this.payload = start.rawPayload();
  }

  @Override
  public String query() {
    return this.payload.get("query").textValue();
  }

  @Override
  public ObjectNode variables() {
    return (ObjectNode) this.payload.get("variables");
  }

  @Override
  public ObjectNode extensions() {
    return (ObjectNode) this.payload.get("extensions");
  }

  @Override
  public String operationName() {
    return this.payload.get("operationName").textValue();
  }

  // final ObjectNode content = mapper.getNodeFactory().objectNode();
  //
  // final ObjectNode data = content.putObject("data");
  //
  // final ObjectNode result = data.putObject("newItemCreated");
  // result.put("id", 1234);
  // result.put("__typename", "Item");

  // this.writer.onNext(SimpleGQLWSFrame.data(start.id(), content));

  // Flowable.interval(5, TimeUnit.SECONDS).map(e -> SimpleGQLWSFrame.data(start.id(),
  // content)).subscribe(this.writer);

}
