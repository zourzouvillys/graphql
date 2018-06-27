package io.zrz.graphql.zulu.engine;

import static java.lang.invoke.MethodHandles.constant;
import static java.lang.invoke.MethodHandles.dropArguments;
import static java.lang.invoke.MethodHandles.guardWithTest;
import static java.lang.invoke.MethodHandles.publicLookup;
import static java.lang.invoke.MethodType.methodType;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

import com.google.common.reflect.TypeToken;

import io.zrz.graphql.zulu.executable.ExecutableReceiverType;
import io.zrz.graphql.zulu.executable.ExecutableTypeUse;

/**
 * a method handle along with specific type information, for generics.
 *
 * @author theo
 *
 */
public class TypeTokenMethodHandle {

  private final MethodHandle handle;

  public TypeTokenMethodHandle(final MethodHandle handle) {
    this.handle = handle;
  }

  public MethodHandle handle() {
    return this.handle;
  }

  public MethodType type() {
    return this.handle.type();
  }

  public TypeTokenMethodHandle insertArguments(final int i, final Object... values) {
    return new TypeTokenMethodHandle(MethodHandles.insertArguments(this.handle(), i, values));
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

  public TypeTokenMethodHandle insertArgument(final int argnum, final String parameterName, final ExecutableTypeUse targetType) {

    final MethodHandle paramProvider = null;

    MethodHandle mapped = MethodHandles.collectArguments(this.handle(), argnum, paramProvider);

    mapped = MethodHandles.permuteArguments(
        mapped,
        mapped.type().dropParameterTypes(2, 3), // new handle type with 1 less param
        0,
        1,
        0);

    return new TypeTokenMethodHandle(mapped);

  }

  /**
   * guards invocation of this handle so it will return null if the receiver is not of the specified type.
   *
   * @param currentType
   *                       The type of the node
   * @param receiverType
   *                       The type the node should be cast to, or null if not that value.
   *
   * @return
   */

  public TypeTokenMethodHandle guardReceiverType(final TypeToken<?> currentType, final ExecutableReceiverType receiverType) {

    final MethodType sig = this.handle
        .type()
        .changeParameterType(1, currentType.getRawType());

    MethodHandle assignableTest = this.isAssignableFrom(receiverType.javaType().getRawType(), currentType.getRawType());

    //

    // drop the first parameter (context)
    assignableTest = dropArguments(assignableTest, 0, this.handle.type().parameterType(0));

    final MethodHandle test = dropArguments(
        assignableTest,
        2,
        assignableTest.type()
            .dropParameterTypes(0, 2)
            .parameterArray());

    final MethodHandle guarded = guardWithTest(
        test.asType(sig.changeReturnType(Boolean.TYPE)),
        this.handle.asType(sig),
        dropArguments(constant(this.handle.type().returnType(), null), 0, sig.parameterArray()));

    return new TypeTokenMethodHandle(guarded);

  }

  /**
   * @param class1
   *
   */

  private final MethodHandle isAssignableFrom(final Class<?> klass, final Class<?> upperBound) {

    try {
      return publicLookup()
          .findVirtual(Class.class, "isInstance", methodType(Boolean.TYPE, Object.class))
          .bindTo(klass)
          .asType(methodType(Boolean.TYPE, upperBound));
    }
    catch (NoSuchMethodException | IllegalAccessException e) {
      throw new RuntimeException(e);
    }

  }

}
