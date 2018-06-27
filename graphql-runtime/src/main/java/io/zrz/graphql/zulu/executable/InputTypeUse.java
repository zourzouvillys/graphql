package io.zrz.graphql.zulu.executable;

import java.util.Objects;

import io.zrz.graphql.zulu.JavaInputField;

public class InputTypeUse {

  private final JavaInputMapper inputType;
  private final ExecutableTypeUse typeuse;
  private final boolean nullable;

  public InputTypeUse(final ExecutableInputField field, final JavaInputField spec, final BuildContext types) {

    this.inputType = types.builder().mapInputType(field);

    // if @Nullable, Optional, or has a default value then
    this.nullable = JavaExecutableUtils.isNullableInput(spec);

    this.typeuse = Objects.requireNonNull(
        types.use(
            field,
            this.inputType.modelType(),
            this.inputType.arity(),
            this.nullable));

  }

  public boolean isNullable() {
    return this.nullable;
  }

  public ExecutableTypeUse use() {
    return Objects.requireNonNull(this.typeuse);
  }

}
