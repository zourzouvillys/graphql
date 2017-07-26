package io.joss.graphql.core.schema;

public class DiffereringTypeException extends RuntimeException {

  public DiffereringTypeException(String symbol, String msg) {
    super(msg);
  }

}
