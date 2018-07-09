package io.zrz.zulu.types;

public enum ZPrimitiveScalarType implements ZScalarType {

  STRING,

  INT,

  BOOLEAN,

  DOUBLE;

  @Override
  public ZTypeKind typeKind() {
    return ZTypeKind.SCALAR;
  }

  @Override
  public ZPrimitiveScalarType baseType() {
    return this;
  }

}
