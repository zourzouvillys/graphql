package io.zrz.graphql.zulu.executable;

import java.util.Map;
import java.util.Optional;

import com.google.common.reflect.TypeToken;

public interface ExecutableReceiverType extends ExecutableType, ExecutableElement {

  /**
   * the logical type name.
   */

  @Override
  String typeName();

  /**
   * the fields declared by this type.
   */

  Map<String, ExecutableOutputField> fields();

  /**
   * fields declared directly by this type.
   */

  Optional<ExecutableOutputField> field(String fieldName);

  /**
   * the java type of the receiver.
   */

  TypeToken<?> javaType();

}
