package io.zrz.graphql.zulu.binding;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.google.common.reflect.TypeToken;

import io.zrz.graphql.zulu.JavaInputField;
import io.zrz.graphql.zulu.annotations.GQLField;

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

    GQLField field = param.getAnnotation(GQLField.class);

    if (field != null) {
      if (!StringUtils.isEmpty(field.value())) {
        return field.value();
      }
    }

    return this.param.getName();
  }

  @Override
  public String toString() {
    return "JavaMethodParameter{"
        + "idx=" + index + ", "
        + "name=" + fieldName() + ", "
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

  @Override
  public int index() {
    return index;
  }

  @Override
  public <T extends Annotation> Optional<T> annotation(Class<T> klass) {
    return Optional.ofNullable(param.getAnnotation(klass));
  }

}
