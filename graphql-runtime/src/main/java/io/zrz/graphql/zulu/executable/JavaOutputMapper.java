package io.zrz.graphql.zulu.executable;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.IntFunction;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.reflect.TypeParameter;
import com.google.common.reflect.TypeToken;

import io.zrz.graphql.zulu.ZuluUtils;
import io.zrz.graphql.zulu.executable.typehandlers.IterableHandler;
import io.zrz.graphql.zulu.executable.typehandlers.ReturnTypeHandlerFactory.ReturnTypeHandler;
import io.zrz.graphql.zulu.executable.typehandlers.StreamHandler;

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

  public static final Method M_streamToArray = ZuluUtils.getMethod(Stream.class, "toArray", IntFunction.class);

  public static final MethodHandle MH_newArray = ZuluUtils.getMethodHandle(
      MethodHandles.lookup(),
      JavaOutputMapper.class,
      "newArray",
      Class.class,
      Integer.TYPE);

  public static final MethodHandle MH_streamToArray = ZuluUtils.unreflect(M_streamToArray);

  /**
   * runtime handle for unwrapping an Iterable to an array.
   */

  private static final MethodHandle MH_fromIterable = ZuluUtils
      .unreflect(
          MethodHandles.lookup(),
          JavaOutputMapper.class,
          "fromIterable",
          Iterable.class,
          Class.class);

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
   * just adjust the type registration.
   *
   * @param field
   * @param javaType
   */

  JavaOutputMapper(final ExecutableOutputField field, final TypeToken<?> javaType) {
    Preconditions.checkArgument(!javaType.getRawType().equals(Object.class));
    // Preconditions.checkArgument(!TypeVariable.class.isAssignableFrom(javaType.getType().getClass()));
    this.javaType = javaType;
    this.modelType = javaType;
    this.apply = UnaryOperator.identity();
  }

  JavaOutputMapper(final JavaOutputMapper parent, final TypeToken<?> modelType) {
    Preconditions.checkArgument(!modelType.getRawType().equals(Object.class), parent);
    // Preconditions.checkArgument(!TypeVariable.class.isAssignableFrom(modelType.getType().getClass()));
    this.javaType = parent.javaType;
    this.modelType = modelType;
    this.apply = parent.apply;
    this.arity = parent.arity;
  }

  JavaOutputMapper(final JavaOutputMapper parent, final TypeToken<?> modelType, final UnaryOperator<MethodHandle> transformer) {
    Preconditions.checkArgument(!modelType.getRawType().equals(Object.class), parent);
    // Preconditions.checkArgument(!TypeVariable.class.isAssignableFrom(modelType.getType().getClass()));
    this.javaType = modelType;
    this.modelType = modelType;
    this.apply = handle -> transformer.apply(parent.apply.apply(handle));
    this.arity = parent.arity;
  }

  JavaOutputMapper(final JavaOutputMapper parent, final TypeToken<?> modelType, final UnaryOperator<MethodHandle> transformer, final int arity) {
    Preconditions.checkArgument(!modelType.getRawType().equals(Object.class), parent);
    // Preconditions.checkArgument(!TypeVariable.class.isAssignableFrom(modelType.getType().getClass()), modelType);
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
   * unwrap this type to the next supported type.
   */

  JavaOutputMapper unwrap() {
    try {

      final TypeToken<?> wrapped = this.javaType;

      if (wrapped.getType() instanceof WildcardType) {

        // the wildcard type for a return can be mapped to the bounds.

        final WildcardType type = (WildcardType) wrapped.getType();

        Preconditions.checkArgument(
            type.getLowerBounds().length == 1,
            "currently only support single lower bound in return type");

        return this.unwrap(TypeToken.of(type.getLowerBounds()[0]), UnaryOperator.identity());

      }
      else if (wrapped.getType() instanceof TypeVariable) {

        final TypeVariable<?> var = (TypeVariable<?>) wrapped.getType();

        Preconditions.checkArgument(
            var.getBounds().length == 1,
            "currently only support single bound in return type");

        return this.unwrap(TypeToken.of(var.getBounds()[0]), UnaryOperator.identity());

      }

      // array types...
      if (wrapped.isArray()) {

        return this.unwrapArray(wrapped.getComponentType());

      }

      else if (wrapped.isSubtypeOf(Iterable.class)) {

        final ReturnTypeHandler<?> handler = new IterableHandler<>()
            .createHandler(wrapped);

        return new JavaOutputMapper(
            this,
            handler.unwrap(),
            target -> MethodHandles.filterReturnValue(target, handler.adapt()),
            handler.arity()
        //
        )
            .unwrap();

      }

      else if (wrapped.isSubtypeOf(Stream.class)) {

        final ReturnTypeHandler<?> handler = new StreamHandler<>().createHandler(wrapped);

        System.err.println(wrapped);
        System.err.println(handler);
        System.err.println(handler.unwrap());

        return new JavaOutputMapper(
            this,
            handler.unwrap(),
            target -> MethodHandles.filterReturnValue(target, handler.adapt()),
            handler.arity()).unwrap();

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

  public static <T> T[] newArray(final Class<T> componentType, final int length) {
    return (T[]) Array.newInstance(componentType, length);
  }

  @SuppressWarnings("serial")
  static <K> TypeToken<Stream<K>> streamOf(final TypeToken<K> keyType) {
    return new TypeToken<Stream<K>>() {}.where(new TypeParameter<K>() {}, keyType);
  }

  @Override
  public String toString() {
    return this.javaType.toString();
  }

  /**
   * if the return value can be null.
   */

  public boolean isNullable() {
    return true;
  }

}
