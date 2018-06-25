package io.zrz.graphql.zulu.executable;

import io.zrz.graphql.zulu.LogicalTypeKind;
import io.zrz.zulu.types.ZType;

public interface ExecutableType extends ZType, ExecutableElement {

  LogicalTypeKind logicalKind();

  String typeName();

  /**
   *
   * @return
   */

  @Override
  default String documentation() {
    return null;
  }

}
