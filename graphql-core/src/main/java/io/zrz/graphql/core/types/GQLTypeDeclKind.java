package io.zrz.graphql.core.types;

public enum GQLTypeDeclKind {

  SCALAR,

  OBJECT,

  INTERFACE,

  UNION,

  ENUM,

  INPUT_OBJECT,;

  public GQLTypeKind toTypeKind() {
    switch (this) {
      case ENUM:
        return GQLTypeKind.ENUM;
      case INPUT_OBJECT:
        return GQLTypeKind.INPUT_OBJECT;
      case INTERFACE:
        return GQLTypeKind.INTERFACE;
      case OBJECT:
        return GQLTypeKind.OBJECT;
      case SCALAR:
        return GQLTypeKind.SCALAR;
      case UNION:
        return GQLTypeKind.UNION;
      default:
        break;
    }
    throw new IllegalArgumentException(name());
  }

}
