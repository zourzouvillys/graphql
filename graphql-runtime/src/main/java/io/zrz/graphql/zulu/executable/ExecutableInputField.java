package io.zrz.graphql.zulu.executable;

import com.google.common.reflect.TypeToken;

import io.zrz.graphql.zulu.JavaInputField;
import io.zrz.zulu.types.ZField;

/**
 * a single input field in the parameters.
 * 
 * @author theo
 *
 */

public final class ExecutableInputField implements ZField, ExecutableElement {

  private String name;
  private TypeToken<?> javaType;
  private ExecutableTypeUse fieldType;

  public ExecutableInputField(ExecutableOutputField field, JavaInputField spec, BuildContext types) {
    this.name = spec.fieldName();
    this.javaType = spec.inputType();
    this.fieldType = types.use(this, javaType);
  }

  public TypeToken<?> javaType() {
    return this.javaType;
  }

  /**
   * the name of this parameter
   */

  public String fieldName() {
    return this.name;
  }

  /**
   * the type for this parameter.
   */

  @Override
  public ExecutableTypeUse fieldType() {
    return this.fieldType;
  }

}
