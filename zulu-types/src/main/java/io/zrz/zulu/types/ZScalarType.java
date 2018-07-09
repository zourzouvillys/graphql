package io.zrz.zulu.types;

public interface ZScalarType extends ZType {

  /**
   * when reading or writing a ZValue of this type, what it looks like.
   */

  ZPrimitiveScalarType baseType();

}
