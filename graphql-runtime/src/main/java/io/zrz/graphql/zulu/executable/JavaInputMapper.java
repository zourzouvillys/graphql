package io.zrz.graphql.zulu.executable;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import com.google.common.reflect.TypeToken;

import io.zrz.graphql.zulu.ZuluUtils;

/**
 * maps types needed for input to a normalized version.
 *
 * @author theo
 *
 */

public class JavaInputMapper {

  private final ExecutableInputField field;
  private final TypeToken<?> javaType;
  private final TypeToken<?> modelType;
  private final int arity;

  public JavaInputMapper(final ExecutableInputField field) {
    this.field = field;
    this.javaType = field.javaType();
    this.modelType = field.javaType();
    this.arity = 0;
  }

  public JavaInputMapper(final JavaInputMapper parent, final TypeToken<?> returnType, final int arity) {
    this.field = parent.field;
    this.javaType = returnType;
    this.modelType = returnType;
    this.arity = arity;
  }

  public TypeToken<?> modelType() {
    return this.modelType;
  }

  public int arity() {
    return this.arity;
  }

  JavaInputMapper unwrapWith(final Method method) {

    final TypeToken<?> returnType = this.javaType.resolveType(method.getGenericReturnType());

    return this.unwrapWith(method, returnType, this.arity);

  }

  JavaInputMapper unwrapWith(final Method method, final TypeToken<?> returnType, final int arity) {

    final MethodHandle filter = ZuluUtils.unreflect(MethodHandles.publicLookup(), method);

    // we need to cast the return type too, as generic getters will fail without.
    final MethodHandle actualFilter = filter
        .asType(filter.type().changeReturnType(returnType.getRawType()));

    return new JavaInputMapper(
        this,
        returnType,
        this.arity + arity)
            .unwrap();
  }

  private MethodHandle applyTarget(final MethodHandle target, final Method method) {
    return target.asType(target.type().changeReturnType(method.getDeclaringClass()));
  }

  JavaInputMapper unwrap(final TypeToken<?> type) {
    return new JavaInputMapper(this, type, this.arity).unwrap();
  }

  JavaInputMapper unwrapArray(final TypeToken<?> type) {
    return new JavaInputMapper(this, type, this.arity + 1).unwrap();
  }

  JavaInputMapper unwrap() {

    try {

      final TypeToken<?> wrapped = this.javaType;

      // array types...
      if (wrapped.isArray()) {

        //
        return this.unwrapArray(wrapped.getComponentType());

      }
      // else if (wrapped.isSubtypeOf(Iterable.class)) {
      //
      // // return unwrapWith(Iterable.class.getMethod("iterator"));
      //
      // }
      // else if (wrapped.isSubtypeOf(Iterator.class)) {
      //
      // // the component type.
      // final TypeToken<?> componentType =
      // wrapped.resolveType(Iterator.class.getMethod("next").getGenericReturnType());
      //
      // // find the toArray method.
      // final Method method = Iterators.class.getMethod("toArray", Iterator.class, wrapped.getRawType().getClass());
      //
      // // the return type.
      // final TypeToken<?> returnType = TypeToken.of(Array.newInstance(componentType.getRawType(), 0).getClass());
      //
      // }

      // other stuffs ...
      else if (wrapped.isSubtypeOf(Optional.class)) {

        return this.unwrapWith(Optional.class.getMethod("get"));

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
