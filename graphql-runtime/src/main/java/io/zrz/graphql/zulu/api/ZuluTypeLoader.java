package io.zrz.graphql.zulu.api;

import java.lang.reflect.Type;

import io.zrz.graphql.zulu.executable.ExecutableSchemaBuilder.TypeRegistry;

/**
 * used to convert an unknown java type token into a GraphQL type.
 *
 * @author theo
 *
 */
public interface ZuluTypeLoader {

  /**
   * if this type loader can generate a symbol from the provided type, else returns null.
   *
   * @param type
   */

  ZuluSymbol load(Type type, TypeRegistry registry);

}
