package io.zrz.graphql.zulu.executable;

import java.lang.invoke.MethodHandle;

import com.google.common.reflect.TypeToken;

public class ReturnTypeUse {

  private final ExecutableTypeUse typeuse;
  private final JavaOutputMapper returnType;

  public ReturnTypeUse(final ExecutableOutputField field, final BuildContext types, final TypeToken<?> returnType) {

    // mapper for the return type.
    this.returnType = types.builder().mapReturnType(
        field,
        returnType);

    // make sure we register usage of this type, and store it.
    this.typeuse = types.use(
        field,
        this.returnType.modelType(),
        this.returnType.returnTypeArity(),
        this.returnType.isNullable());

  }

  public ExecutableTypeUse use() {
    return this.typeuse;
  }

  public MethodHandle filter(final MethodHandle target) {
    return this.returnType.applyTo(target);
  }

  public int arity() {
    return this.returnType.returnTypeArity();
  }

}
