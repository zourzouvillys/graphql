package io.zrz.zulu.server.netty.ws;

public class UnknownGQLWSFrameKind implements GQLWSFrameKind {

  private final String type;

  public UnknownGQLWSFrameKind(final String value) {
    this.type = value;
  }

  @Override
  public String kindName() {
    return this.type;
  }

}
