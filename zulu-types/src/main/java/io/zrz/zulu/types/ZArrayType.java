package io.zrz.zulu.types;

public interface ZArrayType extends ZType {

  /**
   * the type of each component in the array.
   * 
   * @return
   */

  ZTypeUse componentType();

  /**
   * 
   */

  @Override
  default ZTypeKind typeKind() {
    return ZTypeKind.ARRAY;
  }

}
