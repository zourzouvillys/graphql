package io.zrz.graphql.zulu.engine;

public interface ZuluScopedContext<T> {

  T get();

  void complete();

  void error();

}
