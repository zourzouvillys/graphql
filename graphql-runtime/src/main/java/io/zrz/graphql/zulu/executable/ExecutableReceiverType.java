package io.zrz.graphql.zulu.executable;

import java.util.Map;
import java.util.Optional;

import com.google.common.reflect.TypeToken;

public interface ExecutableReceiverType extends ExecutableType, ExecutableElement {

  @Override
  String typeName();

  Map<String, ExecutableOutputField> fields();

  Optional<ExecutableOutputField> field(String fieldName);

  TypeToken<?> javaType();

}
