package io.zrz.graphql.zulu.api;

import java.lang.reflect.Type;

import io.zrz.zulu.types.ZType;

public interface ZuluTypeBinder {

  /**
   * when an input field is needed, this converts to a list of properties and their type as well as a handle for
   * converting from input to the java type.
   */

  ZType scan(Type type);

}
