package io.zrz.graphql.zulu.executable;

import com.google.common.reflect.TypeToken;

public interface InvocationCaller {

  /**
   * accepted return types.
   */

  TypeToken<?> returnTypes();

  /**
   * parameters.
   */

}
