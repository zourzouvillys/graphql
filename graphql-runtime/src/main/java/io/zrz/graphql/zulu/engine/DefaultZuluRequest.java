package io.zrz.graphql.zulu.engine;

public class DefaultZuluRequest extends ZuluRequest {

  public DefaultZuluRequest() {
    super(EmptyParameterReader.INSTANCE);
  }

}
