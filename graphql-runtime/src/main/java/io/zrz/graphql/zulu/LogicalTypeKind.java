package io.zrz.graphql.zulu;

import io.zrz.graphql.zulu.annotations.GQLType.Kind;

/**
 *
 * @author theo
 *
 */

public enum LogicalTypeKind {

  INPUT,

  ENUM,

  INTERFACE,

  OUTPUT,

  UNION,

  SCALAR;

  public static LogicalTypeKind from(final Kind kind) {
    switch (kind) {
      case ENUM:
        return ENUM;
      case INPUT:
        return INPUT;
      case INTERFACE:
        return INTERFACE;
      case OBJECT:
        return OUTPUT;
      case SCALAR:
        return SCALAR;
      case UNION:
        return UNION;
      default:
        throw new IllegalArgumentException(kind.name());
    }
  }

}
