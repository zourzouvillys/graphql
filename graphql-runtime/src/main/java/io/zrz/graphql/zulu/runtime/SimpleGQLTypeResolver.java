package io.zrz.graphql.zulu.runtime;

import java.util.HashMap;
import java.util.Map;

import io.zrz.zulu.types.ZType;

/**
 * a type resolver implementation which uses a map for resolving types.
 * 
 * @author theo
 *
 */

public class SimpleGQLTypeResolver extends DefaultGQLTypeResolver {

  private Map<String, ZType> types = new HashMap<>();

  /**
   * adds a named type to the resolver.
   * 
   * @param typeName
   * @param type
   */

  public void put(String typeName, ZType type) {
    types.put(typeName, type);
  }

  /**
   * removes the type with the specified name from the resolver.
   * 
   * @param typeName
   * @return
   */

  public ZType remove(String typeName) {
    return types.remove(typeName);
  }

  @Override
  public ZType resolve(String typeName) {
    return super.resolve(typeName);
  }

}
