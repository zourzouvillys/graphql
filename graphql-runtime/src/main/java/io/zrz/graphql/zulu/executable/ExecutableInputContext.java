package io.zrz.graphql.zulu.executable;

import java.lang.annotation.Annotation;
import java.util.Objects;
import java.util.Optional;

import com.google.common.reflect.TypeToken;

import io.zrz.graphql.zulu.JavaInputField;

/**
 * a single input field in the parameters.
 *
 * @author theo
 *
 */

public final class ExecutableInputContext implements ExecutableElement {

  private final String name;
  private final TypeToken<?> javaType;
  private final ExecutableOutputField field;
  private final int index;
  private final JavaInputField param;

  public ExecutableInputContext(final ExecutableOutputField field, final JavaInputField spec, final BuildContext types) {
    this.field = field;
    this.name = spec.fieldName();
    this.javaType = Objects.requireNonNull(spec.inputType());
    this.index = spec.index();
    this.param = spec;
  }

  public ExecutableReceiverType enclosingType() {
    return this.field.receiverType();
  }

  public ExecutableOutputField enclosingField() {
    return this.field;
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
   *
   */

  @Override
  public String toString() {
    return "context parameter " + this.index + " " + this.name + " of " + this.field.receiverType().typeName() + "." + this.field.fieldName()
        + ": (native " + this.javaType + ")";
  }

  public int index() {
    return this.index;
  }

  public <T extends Annotation> Optional<T> annotation(final Class<T> klass) {
    return this.param.annotation(klass);
  }

}
