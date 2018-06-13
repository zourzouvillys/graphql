package io.zrz.graphql.zulu.binding;

import java.lang.reflect.Parameter;
import java.util.Arrays;

import com.google.common.reflect.TypeToken;

import io.zrz.graphql.zulu.JavaInputField;

/**
 * a java bound method parameter.
 */

public class JavaBindingParameter implements JavaInputField {

  private JavaBindingMethodAnalysis method;
  private int index;
  private Parameter param;
  private TypeToken<?> type;

  public JavaBindingParameter(JavaBindingMethodAnalysis method, int index, Parameter param, TypeToken<?> type) {
    this.method = method;
    this.index = index;
    this.param = param;
    this.type = type;
  }

  @Override
  public String fieldName() {
    return this.param.getName();
  }

  @Override
  public String toString() {
    return "JavaMethodParameter{"
        + "idx=" + index + ", "
        + "name=" + param.getName() + ", "
        + "type=" + inputType() + ", "
        + "typeparams=" + inputTypeAnnotations() + ", "
        + "annotations=" + annotations() + "}";
  }

  public String annotations() {
    return Arrays.toString(this.param.getAnnotations());
  }

  /**
   * the actual java type expected for this input parameter.
   */

  @Override
  public TypeToken<?> inputType() {
    return this.type;
  }

  private String inputTypeAnnotations() {
    return Arrays.toString(this.param.getAnnotatedType().getAnnotations());
  }

}
