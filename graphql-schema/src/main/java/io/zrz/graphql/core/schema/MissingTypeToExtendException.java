package io.zrz.graphql.core.schema;

public class MissingTypeToExtendException extends RuntimeException {

  public MissingTypeToExtendException(String symbol, String msg) {
    super(msg);
  }

}
