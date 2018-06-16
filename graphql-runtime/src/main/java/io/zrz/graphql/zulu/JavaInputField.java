
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

  /**
   * the index of this parameter.
   * 
   * note that this may not directly map to the underlying declared method, but will map to the handler.
   * 
   * for example, a static extension method will not include the extension context value as a parameter and instead
   * handle it internally.
   * 
   */

  int index();

  /**
   * 
   */

  <T extends Annotation> Optional<T> annotation(Class<T> klass);

}
