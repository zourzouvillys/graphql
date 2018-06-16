
package io.zrz.graphql.zulu;

import java.lang.annotation.Annotation;
import java.util.Optional;

import com.google.common.reflect.TypeToken;

/**
 * specification for a java input field.
 */

public interface JavaInputField {

  /**
   * the field name.
   */

  String fieldName();

  /**
   * the java type of the parameter.
   */

  TypeToken<?> inputType();

  int index();

  <T extends Annotation> Optional<T> annotation(Class<T> klass);

}
