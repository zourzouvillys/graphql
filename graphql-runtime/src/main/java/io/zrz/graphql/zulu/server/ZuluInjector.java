package io.zrz.graphql.zulu.server;

import com.google.common.reflect.TypeToken;

public interface ZuluInjector {

  <T> T newInstance(TypeToken<T> javaType);

}
