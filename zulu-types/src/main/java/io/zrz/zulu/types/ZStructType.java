package io.zrz.zulu.types;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public interface ZStructType extends ZType {

  /**
   * returns the field names, in order.
   */

  default Stream<String> fieldNames() {
    return fields().keySet().stream().sequential();
  }

  /**
   * each of the fields in this structure. the entries will be ordered as they are provided.
   */

  Map<String, ? extends ZField> fields();

  /**
   * the index of the specified field name.
   */

  default int indexOf(String fieldName) {
    return new ArrayList<>(fields().keySet()).indexOf(fieldName);
  }

  /**
   * looks up the specified field.
   */

  default Optional<? extends ZField> field(String name) {
    return Optional.ofNullable(this.fields().get(name));
  }

  /**
   * looks up the specified field.
   */

  default boolean contains(String name) {
    return this.fields().containsKey(name);
  }

  /**
   * 
   */

  @Override
  default ZTypeKind typeKind() {
    return ZTypeKind.STRUCT;
  }

}
