package io.zrz.zulu.types;

import java.util.List;

public interface ZEnumType extends ZType {

  /**
   * the enum values for this type.
   */

  List<String> enumValues();

  /**
   * 
   */

  @Override
  default ZTypeKind typeKind() {
    return ZTypeKind.ENUM;
  }

}
