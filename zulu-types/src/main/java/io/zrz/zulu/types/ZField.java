package io.zrz.zulu.types;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import io.zrz.zulu.values.ZValue;

public interface ZField {

  /**
   * the name of this field in the owning type.
   */

  String fieldName();

  /**
   * the datatype of this field.
   */

  ZTypeUse fieldType();

  /**
   * annotations on this field. note that this is distinct from annotations on the type usage.
   */

  default List<ZAnnotation> annotations() {
    return Collections.emptyList();
  }

  /**
   * the default value for this field.
   */

  default Optional<ZValue> defaultValue() {
    return Optional.empty();
  }

  /**
   * the constant value for this field, if it has one.
   *
   * if the field has a constant value, then it will not have a default value.
   *
   */

  default Optional<ZValue> constantValue() {
    return Optional.empty();
  }

  /**
   * if this field needs to be provided.
   */

  default boolean isOptional() {
    return false;
  }

}
