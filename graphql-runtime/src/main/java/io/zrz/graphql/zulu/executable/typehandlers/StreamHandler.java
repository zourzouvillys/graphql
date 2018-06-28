package io.zrz.graphql.zulu.executable.typehandlers;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.TypeVariable;
import java.util.function.IntFunction;
import java.util.stream.Stream;

import com.google.common.reflect.TypeToken;

import io.zrz.graphql.zulu.ZuluUtils;

public class StreamHandler<T> implements ReturnTypeHandlerFactory<Stream<T>, T> {

  private static final TypeVariable<?> STREAM_TYPE_PARAM = Stream.class.getTypeParameters()[0];

  public static final Method M_streamToArray = ZuluUtils.getMethod(Stream.class, "toArray", IntFunction.class);

  public static final MethodHandle MH_streamToArray = ZuluUtils.unreflect(M_streamToArray);

  /**
   * given a type, provides the real type.
   */

  @Override
  public ReturnTypeHandler<T> createHandler(final TypeToken<?> type) {
    if (!type.isSubtypeOf(Stream.class)) {
      return null;
    }
    return new Unwrapper<>(type.getSupertype((Class) Stream.class).resolveType(STREAM_TYPE_PARAM));
  }

  private static <T> IntFunction<Object> newInstanceProvider(final Class<T> componentType) {
    return length -> Array.newInstance(componentType, length);
  }

  private static class Unwrapper<T> implements ReturnTypeHandler<T> {

    private final TypeToken<?> componentType;
    private final TypeToken<?> returnType;
    private final Class<?> rawComponentType;
    private final MethodHandle transformer;

    Unwrapper(final TypeToken<?> componentType) {

      this.componentType = componentType;
      this.returnType = TypeToken.of(Array.newInstance(this.componentType.getRawType(), 0).getClass());
      this.rawComponentType = this.componentType.getRawType();

      MethodHandle actualFilter = MethodHandles.insertArguments(
          MH_streamToArray,
          1,
          newInstanceProvider(this.rawComponentType));

      actualFilter = actualFilter.asType(actualFilter.type().changeReturnType(this.returnType.getRawType()));

      this.transformer = actualFilter;

    }

    @Override
    public TypeToken<?> unwrap() {
      return this.componentType;
    }

    @Override
    public MethodHandle adapt() {
      return this.transformer;
    }

    @Override
    public int arity() {
      return 1;
    }

  }

}
