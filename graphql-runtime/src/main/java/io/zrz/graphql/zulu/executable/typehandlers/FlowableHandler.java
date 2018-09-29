package io.zrz.graphql.zulu.executable.typehandlers;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.lang.reflect.TypeVariable;

import com.google.common.reflect.TypeToken;

import io.reactivex.Flowable;
import io.zrz.graphql.zulu.ZuluUtils;

public class FlowableHandler<T> implements ReturnTypeHandlerFactory<Flowable<T>, T> {

  private static final TypeVariable<?> TYPE_PARAM = Flowable.class.getTypeParameters()[0];

  public static final Method M_streamToArray = ZuluUtils.getMethod(Flowable.class, "blockingFirst");

  public static final MethodHandle MH_streamToArray = ZuluUtils.unreflect(M_streamToArray);

  /**
   * given a type, provides the real type.
   */

  @Override
  public ReturnTypeHandler<T> createHandler(final TypeToken<?> type) {
    if (!type.isSubtypeOf(Flowable.class)) {
      return null;
    }
    return new Unwrapper<>(type.getSupertype((Class) Flowable.class).resolveType(TYPE_PARAM));
  }

  private static class Unwrapper<T> implements ReturnTypeHandler<T> {

    private final TypeToken<?> componentType;
    private final MethodHandle transformer;

    Unwrapper(final TypeToken<?> componentType) {
      this.componentType = componentType;
      this.transformer = MethodHandles.identity(Flowable.class);
    }

    @Override
    public TypeToken<?> unwrap() {
      return this.componentType;
    }

    @Override
    public MethodHandle adapt() {
      return this.transformer;
    }

    // note: returns as a non array for subscriptions. need to fix so it can be used with queries/mutations.
    @Override
    public int arity() {
      return 0;
    }

  }

}
