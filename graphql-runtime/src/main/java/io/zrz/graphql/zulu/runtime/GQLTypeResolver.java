package io.zrz.graphql.zulu.runtime;

import io.zrz.zulu.types.ZType;

/**
 * provides a mechanism for the runtime to resolve type.
 * 
 * 
 * @author theo
 *
 */
public interface GQLTypeResolver {

  /**
   * given a type name, returns the type definition for it.
   * 
   * @param typeName
   *          The type name to resolve.
   * 
   * @return
   */

  ZType resolve(String typeName);

}
