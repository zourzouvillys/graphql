package io.zrz.graphql.zulu.executable;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.UnaryOperator;

import com.google.common.collect.Iterables;
import com.google.common.reflect.TypeToken;

import io.zrz.graphql.zulu.ZuluUtils;

/**
 * extract the actual type that this type references. it removes any wrapping magic/sugar, e.g Optional, Flowable,
 * Single, CompletableFuture, List etc for providing the actual model type for GraphQL binding, and generates a handle
 * which can be used for the most effecient negotiation between invocations without needing to do pointless transcoding.
 *
 * this only deals with the GraphQL type, execution and flow components to normalize the types that consumers need to
 * deal with, it doesn't deal with conversion between different types based on content or other properties.
 *
 */

class JavaOutputMapper {

  /**
   * the underlying type that we will receive an instance of.
   */

  private final TypeToken<?> javaType;

  /**
   * the logical model type that this return type represents.
   */

  private final TypeToken<?> modelType;

  /**
   * operator which transforms the method handle to the new type.
   */

  private final UnaryOperator<MethodHandle> apply;

  // for arrays.
  private int arity = 0;

  /**
   *
   * @param field
   * @param javaType
   */

  JavaOutputMapper(final ExecutableOutputField field, final TypeToken<?> javaType) {
    this.javaType = javaType;
    this.modelType = javaType;
    this.apply = UnaryOperator.identity();
  }

  JavaOutputMapper(final JavaOutputMapper parent, final TypeToken<?> modelType) {
    this.javaType = parent.javaType;
    this.modelType = modelType;
    this.apply = parent.apply;
    this.arity = parent.arity;
  }

  JavaOutputMapper(final JavaOutputMapper parent, final TypeToken<?> modelType, final UnaryOperator<MethodHandle> transformer) {
    this.javaType = modelType;
    this.modelType = modelType;
    this.apply = handle -> transformer.apply(parent.apply.apply(handle));
    this.arity = parent.arity;
  }

  JavaOutputMapper(final JavaOutputMapper parent, final TypeToken<?> modelType, final UnaryOperator<MethodHandle> transformer, final int arity) {
    this.javaType = modelType;
    this.modelType = modelType;
    this.apply = handle -> transformer.apply(parent.apply.apply(handle));
    this.arity = arity;
  }

  /**
   *
   * @param target
   * @return
   */

  public MethodHandle applyTo(final MethodHandle target) {
    return this.apply.apply(target);
  }

  /**
   * the type which will be returned when invoking.
   *
   * this differs from the underlying type (which is what is returned directly from the invocation) by being the type
   * returned after the type mappers are applied.
   *
   * so a
   *
   */

  public TypeToken<?> returnType() {
    return this.modelType;
  }

  /**
   * how many dimensions the return type has. 0 for none.
   */

  public int returnTypeArity() {
    return this.arity;
  }

  /**
   * the model type represented by this java type.
   *
   * for lists, the type should be converted to an array. @NonNull or @Nullable should be provided to indicate it's
   * nullability.
   *
   * the returned type must have a mapping to the GraphQL type system.
   *
   */

  public TypeToken<?> modelType() {
    return this.modelType;
  }

  JavaOutputMapper unwrap(final TypeToken<?> type, final UnaryOperator<MethodHandle> transformer) {
    return new JavaOutputMapper(this, type, transformer).unwrap();
  }

  JavaOutputMapper unwrap(final TypeToken<?> type) {
    return new JavaOutputMapper(this, type, this.apply).unwrap();
  }

  JavaOutputMapper unwrapArray(final TypeToken<?> type) {
    return new JavaOutputMapper(this, type, this.apply, this.arity + 1).unwrap();
  }

  JavaOutputMapper unwrapWith(final Method method) {

    final TypeToken<?> returnType = this.javaType.resolveType(method.getGenericReturnType());

    return this.unwrapWith(method, returnType, this.arity);

  }

  JavaOutputMapper unwrapArrayWith(final Method method) {
    final TypeToken<?> returnType = this.javaType.resolveType(method.getGenericReturnType());
    return this.unwrapWith(method, returnType, this.arity + 1);
  }

  JavaOutputMapper unwrapWith(final Method method, final TypeToken<?> returnType, final int arity) {

    final MethodHandle filter = ZuluUtils.unreflect(MethodHandles.publicLookup(), method);

    // we need to cast the return type too, as generic getters will fail.
    final MethodHandle actualFilter = filter
        .asType(filter.type().changeReturnType(returnType.getRawType()));

    return new JavaOutputMapper(
        this,
        returnType,
        target -> MethodHandles.filterReturnValue(this.applyTarget(target, method), actualFilter),
        this.arity + arity).unwrap();
  }

  private MethodHandle applyTarget(final MethodHandle target, final Method method) {
    return target.asType(target.type().changeReturnType(method.getDeclaringClass()));
  }

  @SuppressWarnings("unused")
  private static <T extends Object> T[] fromIterable(final Iterable<T> iter, final Class<T> klass) {
    if (iter == null) {
      return null;
    }
    return Iterables.toArray(iter, klass);
  }

  /**
   * runtime handle for unwrapping an Iterable to an array.
   */

  private final MethodHandle MH_fromIterable = ZuluUtils
      .unreflect(
          MethodHandles.lookup(),
          JavaOutputMapper.class,
          "fromIterable",
          Iterable.class,
          Class.class);

  /**
   * unwrap this type to the next supported type.
   */

  JavaOutputMapper unwrap() {
    try {

      final TypeToken<?> wrapped = this.javaType;

      // array types...
      if (wrapped.isArray()) {

        return this.unwrapArray(wrapped.getComponentType());

      }
      else if (wrapped.isSubtypeOf(Iterable.class)) {

        // find the componentType....
        final TypeToken<?> iteratorType = wrapped.resolveType(Iterable.class.getMethod("iterator").getGenericReturnType());
        final TypeToken<?> componentType = iteratorType.resolveType(Iterator.class.getMethod("next").getGenericReturnType());
        final TypeToken<?> returnType = TypeToken.of(Array.newInstance(componentType.getRawType(), 0).getClass());

        // then generate filter to map to a raw array.
        MethodHandle actualFilter = MethodHandles.insertArguments(this.MH_fromIterable, 1, componentType.getRawType());

        actualFilter = actualFilter.asType(
            actualFilter.type()
                .changeParameterType(0, wrapped.getRawType())
                .changeReturnType(returnType.getRawType()));

        final MethodHandle transformer = actualFilter;

        return new JavaOutputMapper(
            this,
            componentType,
            target -> MethodHandles.filterReturnValue(target, transformer),
            this.arity + 1)
                .unwrap();

      }

      // other stuffs ...
      else if (wrapped.isSubtypeOf(Optional.class)) {

        // return unwrapWith(Optional.class.getMethod("get"));

        final TypeToken<?> actualType = wrapped.resolveType(Optional.class.getMethod("get").getGenericReturnType());

        final Method method = Optional.class.getMethod("orElse", Object.class);

        MethodHandle filter = ZuluUtils.unreflect(MethodHandles.publicLookup(), method);

        filter = MethodHandles.insertArguments(filter, 1, new Object[] { null });

        // normalize return type
        final MethodHandle actualFilter = filter
            .asType(filter.type().changeReturnType(actualType.getRawType()));

        return new JavaOutputMapper(
            this,
            actualType,
            target -> MethodHandles.filterReturnValue(this.applyTarget(target, method), actualFilter),
            this.arity + this.arity).unwrap();

      }
      else if (wrapped.isSubtypeOf(CompletableFuture.class)) {
        return this.unwrapWith(CompletableFuture.class.getMethod("get"));
      }

      else if (wrapped.isSubtypeOf(StringBuilder.class)) {
        return this.unwrap(TypeToken.of(String.class));
      }

      return this;

    }
    catch (final Exception ex) {
      throw new RuntimeException(ex);
    }
  }

}
