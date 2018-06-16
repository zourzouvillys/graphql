package io.zrz.graphql.core.types;

public enum GQLTypeKind {

  SCALAR,
  OBJECT,
  INTERFACE,
  UNION,
  ENUM,
  INPUT_OBJECT,

  // .. reference types.
  LIST,
  NON_NULL,

}
