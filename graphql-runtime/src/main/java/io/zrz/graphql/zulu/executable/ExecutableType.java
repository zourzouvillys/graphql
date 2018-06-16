package io.zrz.graphql.zulu.executable;

import io.zrz.graphql.zulu.LogicalTypeKind;
import io.zrz.zulu.types.ZType;

public interface ExecutableType extends ZType {

  LogicalTypeKind logicalKind();

  String typeName();

  default String documentation() {
    return null;
  }

}
