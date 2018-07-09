package io.zrz.zulu.types;

import java.util.List;

public interface ZTupleType extends ZType {

  /**
   * the ordered list of types for this tuple.
   */

  List<ZField> fields();

  /**
   * 
   */

  @Override
  default ZTypeKind typeKind() {
    return ZTypeKind.TUPLE;
  }

}
