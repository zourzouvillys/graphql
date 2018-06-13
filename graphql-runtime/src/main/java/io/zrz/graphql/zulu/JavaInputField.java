
package io.zrz.graphql.zulu;

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

}
