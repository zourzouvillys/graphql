package io.joss.graphql.core.schema;

public class MissingTypeToExtendException extends RuntimeException {

  public MissingTypeToExtendException(String symbol, String msg) {
    super(msg);
  }

}
