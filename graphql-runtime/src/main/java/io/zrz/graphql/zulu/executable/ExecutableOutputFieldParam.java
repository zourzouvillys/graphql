package io.zrz.graphql.zulu.executable;

import java.lang.annotation.Annotation;
import java.util.Objects;
import java.util.Optional;

import org.eclipse.jdt.annotation.NonNull;

import com.google.common.reflect.TypeToken;

import io.zrz.graphql.zulu.JavaInputField;
import io.zrz.zulu.types.ZField;
import io.zrz.zulu.values.ZValue;

/**
 * a single input field in the parameters.
 *
 * @author theo
 *
 */

public final class ExecutableOutputFieldParam implements ZField, ExecutableElement, ExecutableInput {

  private final String name;
  private final TypeToken<?> javaType;
  private final ExecutableOutputField field;
  private final InputTypeUse fieldTypeUse;
  private final int index;
  private final JavaInputField param;

  /**
   *
   * @param field
   * @param spec
   * @param types
   */

  public ExecutableOutputFieldParam(final ExecutableOutputField field, final JavaInputField spec, final BuildContext types) {
    this.field = field;
    this.name = spec.fieldName();
    this.javaType = Objects.requireNonNull(spec.inputType());
    this.index = spec.index();
    this.param = spec;
    this.fieldTypeUse = new InputTypeUse(this, spec, types);
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

  @Override
  public @NonNull String fieldName() {
    return this.name;
  }

  /**
   * the type for this parameter.
   */

  @Override
  public ExecutableTypeUse fieldType() {
    if (this.fieldTypeUse == null) {
      throw new IllegalStateException("field not yet fully initialized");
    }
    return Objects.requireNonNull(this.fieldTypeUse.use());
  }

  /**
   *
   */

  @Override
  public String toString() {
    return "parameter " + this.name + " of " + this.field.receiverType().typeName() + "." + this.field.fieldName()
        + ": " + (this.fieldTypeUse == null ? "<recursive>" : this.fieldType()) + " (native " + this.javaType + ")";
  }

  public int index() {
    return this.index;
  }

  public <T extends Annotation> Optional<T> annotation(final Class<T> klass) {
    return this.param.annotation(klass);
  }

  @Override
  public boolean isNullable() {
    return this.fieldTypeUse.isNullable();
  }

  @Override
  public Optional<ZValue> defaultValue() {
    return ZField.super.defaultValue();
  }

}
