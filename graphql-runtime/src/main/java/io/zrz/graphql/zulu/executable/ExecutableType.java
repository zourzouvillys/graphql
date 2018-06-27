package io.zrz.graphql.zulu.executable;

import com.google.common.reflect.TypeToken;

import io.zrz.graphql.zulu.LogicalTypeKind;
import io.zrz.zulu.types.ZType;

public interface ExecutableType extends ZType, ExecutableElement {

  LogicalTypeKind logicalKind();

  String typeName();

  TypeToken<?> javaType();

  /**
   *
   * @return
   */

  @Override
  default String documentation() {
    return null;
  }

}
