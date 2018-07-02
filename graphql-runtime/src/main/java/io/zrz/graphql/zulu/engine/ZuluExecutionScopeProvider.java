package io.zrz.graphql.zulu.engine;

import java.lang.reflect.Type;

public interface ZuluExecutionScopeProvider<T> {

  ZuluScopedContext<T> createContextValue(Type type);

}
