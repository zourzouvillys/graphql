package io.zrz.zulu.types;

public enum ZTypeKind {

  /**
   * a single value.
   */

  SCALAR,

  /**
   * oredered values of the same type.
   */

  ARRAY,

  /**
   * key/value pairs.
   */

  STRUCT,

  /**
   * an ordered list of unnamed values, e.g a parameter list.
   */

  TUPLE,

  /**
   * TODO: move to scalar?
   */

  ENUM,

  VOID

}
