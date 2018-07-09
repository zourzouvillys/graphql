package io.zrz.zulu.types;

import java.util.stream.Collectors;

public abstract class AbstractZStructType implements ZStructType {

  @Override
  public String toString() {
    return fields()
        .entrySet()
        .stream()
        .map(x -> x.getKey() + ": " + x.getValue())
        .collect(Collectors.joining(", ", "(", ")"));
  }

}
