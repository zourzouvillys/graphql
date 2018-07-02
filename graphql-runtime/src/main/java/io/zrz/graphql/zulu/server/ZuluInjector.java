package io.zrz.graphql.zulu.server;

import java.lang.reflect.Type;

import com.google.common.reflect.TypeToken;

import io.zrz.graphql.zulu.engine.ZuluExecutionScopeProvider;

public interface ZuluInjector {

  <T> T newInstance(TypeToken<T> javaType);

  <T> ZuluExecutionScopeProvider<T> contextProvider(Type type);

}
