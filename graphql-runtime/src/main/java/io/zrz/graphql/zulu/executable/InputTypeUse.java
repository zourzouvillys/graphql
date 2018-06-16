package io.zrz.graphql.zulu.executable;

import java.util.Objects;

import io.zrz.graphql.zulu.JavaInputField;

public class InputTypeUse {

  private JavaInputMapper inputType;
  private ExecutableTypeUse typeuse;

  public InputTypeUse(ExecutableInputField field, JavaInputField spec, BuildContext types) {
    this.inputType = types.builder().mapInputType(field);
    this.typeuse = Objects.requireNonNull(types.use(field, this.inputType.modelType(), this.inputType.arity()));
  }

  public ExecutableTypeUse use() {
    return Objects.requireNonNull(this.typeuse);
  }

}
