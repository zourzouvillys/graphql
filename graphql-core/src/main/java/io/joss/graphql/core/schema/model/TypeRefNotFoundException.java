package io.joss.graphql.core.schema.model;

import java.util.Stack;

public class TypeRefNotFoundException extends RuntimeException {

  public TypeRefNotFoundException(String name, Stack<String> refstack) {
    super(name);
  }

}
