package io.zrz.zulu.types;

import java.util.Optional;

import io.zrz.zulu.values.ZStructValue;

/**
 * an annotation for use with the type system.
 */

public interface ZAnnotation {

  /**
   * the name of this annotation.
   */

  String name();

  /**
   * the value of this annotation, if any is defined.
   */

  Optional<ZStructValue> value();

}
