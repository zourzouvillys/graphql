package io.zrz.graphql.zulu.executable.typehandlers;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Array;
import java.lang.reflect.TypeVariable;

import com.google.common.collect.Iterables;
import com.google.common.reflect.TypeToken;

import io.zrz.graphql.zulu.ZuluUtils;

public class IterableHandler<T> implements ReturnTypeHandlerFactory<Iterable<T>, T> {

  private static final TypeVariable<?> TYPE_PARAM = Iterable.class.getTypeParameters()[0];

  // public static final Method M_streamToArray = ZuluUtils.getMethod(Stream.class, "toArray", IntFunction.class);
  //
  // public static final MethodHandle MH_streamToArray = ZuluUtils.unreflect(M_streamToArray);

  private static final MethodHandle MH_fromIterable = ZuluUtils
      .unreflect(
          MethodHandles.lookup(),
          IterableHandler.class,
          "fromIterable",
          Iterable.class,
          Class.class);

  /**
   * given a type, provides the real type.
   */

  @Override
  public ReturnTypeHandler<T> createHandler(final TypeToken<?> type) {

    if (!type.isSubtypeOf(Iterable.class)) {
      return null;
    }
    final TypeToken iterType = type.getSupertype((Class) Iterable.class);
    return new Unwrapper<>(type, iterType.resolveType(TYPE_PARAM));

  }

  private static class Unwrapper<T> implements ReturnTypeHandler<T> {

    private final TypeToken<?> componentType;
    private final TypeToken<?> returnType;
    // private final Class<?> rawComponentType;
    private final MethodHandle transformer;

    Unwrapper(final TypeToken<?> actualType, final TypeToken<?> componentType) {

      //
      // this.componentType = componentType;
      // this.returnType = TypeToken.of(Array.newInstance(this.componentType.getRawType(), 0).getClass());
      // this.rawComponentType = this.componentType.getRawType();
      //
      // MethodHandle actualFilter = MethodHandles.insertArguments(
      // MH_streamToArray,
      // 1,
      // newInstanceProvider(this.rawComponentType));
      //
      // actualFilter = actualFilter.asType(actualFilter.type().changeReturnType(this.returnType.getRawType()));
      //
      // this.transformer = actualFilter;

      // find the componentType....
      // this.iteratorType = wrapped.resolveType(Iterable.class.getMethod("iterator").getGenericReturnType());

      this.componentType = componentType;
      this.returnType = TypeToken.of(Array.newInstance(this.componentType.getRawType(), 0).getClass());

      // then generate filter to map to a raw array.
      MethodHandle actualFilter = MethodHandles.insertArguments(MH_fromIterable, 1, this.componentType.getRawType());

      actualFilter = actualFilter.asType(
          actualFilter.type()
              .changeParameterType(0, actualType.getRawType())
              .changeReturnType(this.returnType.getRawType()));

      this.transformer = actualFilter;

      // return new JavaOutputMapper(
      // this,
      // componentType,
      // target -> MethodHandles.filterReturnValue(target, transformer),
      // this.arity + 1)
      // .unwrap();

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

  @SuppressWarnings("unused")
  private static <T extends Object> T[] fromIterable(final Iterable<T> iter, final Class<T> klass) {
    if (iter == null) {
      return null;
    }
    return Iterables.toArray(iter, klass);
  }

}
