package io.zrz.graphql.zulu.engine;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

import io.zrz.graphql.zulu.executable.ExecutableTypeUse;

/**
 * a method handle along with specific type information, for generics.
 * 
 * @author theo
 *
 */
public class TypeTokenMethodHandle {

  private MethodHandle handle;

  public TypeTokenMethodHandle(MethodHandle handle) {
    this.handle = handle;
  }

  public MethodHandle handle() {
    return this.handle;
  }

  public MethodType type() {
    return handle.type();
  }

  public TypeTokenMethodHandle insertArguments(int i, Object... values) {
    return new TypeTokenMethodHandle(MethodHandles.insertArguments(handle(), i, values));
  }

  /**
   * insert the specified operation argument as a parameter at the given position.
   * 
   * @param i
   * 
   * @param parameterName
   * @param targetType
   * @return
   */

  public TypeTokenMethodHandle insertArgument(int argnum, String parameterName, ExecutableTypeUse targetType) {

    MethodHandle paramProvider = null;

    MethodHandle mapped = MethodHandles.collectArguments(handle(), argnum, paramProvider);

    mapped = MethodHandles.permuteArguments(
        mapped,
        mapped.type().dropParameterTypes(2, 3), // new handle type with 1 less param
        0,
        1,
        0);

    return new TypeTokenMethodHandle(mapped);

  }

}
