package io.zrz.graphql.zulu.runtime;

import io.zrz.zulu.types.ZPrimitiveScalarType;
import io.zrz.zulu.types.ZType;

/**
 * type resolver which handles the default types (String, Boolean, Int, and Float).
 * 
 * can be used as a base class for other type resolvers.
 * 
 * @author theo
 *
 */

public class DefaultGQLTypeResolver implements GQLTypeResolver {

  @Override
  public ZType resolve(String typeName) {

    switch (typeName) {
      case "String":
        return ZPrimitiveScalarType.STRING;
      case "Boolean":
        return ZPrimitiveScalarType.BOOLEAN;
      case "Int":
        return ZPrimitiveScalarType.INT;
      case "Float":
        return ZPrimitiveScalarType.DOUBLE;
    }

    throw new IllegalArgumentException(typeName);

  }

}
