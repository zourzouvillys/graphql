package io.zrz.graphql.zulu.executable;

import java.util.Map;
import java.util.Optional;

import com.google.common.reflect.TypeToken;

public interface ExecutableReceiverType extends ExecutableType, ExecutableElement {

  @Override
  String typeName();

  Map<String, ExecutableOutputField> fields();

  @Override
  default String documentation() {
    // TODO Auto-generated method stub
    return ExecutableElement.super.documentation();
  }

  Optional<ExecutableOutputField> field(String fieldName);

  TypeToken<?> javaType();

}
