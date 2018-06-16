package io.zrz.graphql.zulu.executable;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import com.google.common.collect.Iterators;
import com.google.common.reflect.TypeToken;

import io.zrz.graphql.zulu.ZuluUtils;

public class JavaInputMapper {

  private ExecutableInputField field;
  private TypeToken<?> javaType;
  private TypeToken<?> modelType;
  private int arity;

  public JavaInputMapper(ExecutableInputField field) {

    this.field = field;
    this.javaType = field.javaType();
    this.modelType = field.javaType();
    this.arity = 0;

  }

  public JavaInputMapper(JavaInputMapper parent, TypeToken<?> returnType, int arity) {

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

  JavaInputMapper unwrapWith(Method method) {

    TypeToken<?> returnType = this.javaType.resolveType(method.getGenericReturnType());

    return unwrapWith(method, returnType, this.arity);

  }

  JavaInputMapper unwrapWith(Method method, TypeToken<?> returnType, int arity) {

    MethodHandle filter = ZuluUtils.unreflect(MethodHandles.publicLookup(), method);

    // we need to cast the return type too, as generic getters will fail without.
    MethodHandle actualFilter = filter
        .asType(filter.type().changeReturnType(returnType.getRawType()));

    return new JavaInputMapper(
        this,
        returnType,
        this.arity + arity)
            .unwrap();
  }

  private MethodHandle applyTarget(MethodHandle target, Method method) {
    return target.asType(target.type().changeReturnType(method.getDeclaringClass()));
  }

  JavaInputMapper unwrap(TypeToken<?> type) {
    return new JavaInputMapper(this, type, this.arity).unwrap();
  }

  JavaInputMapper unwrapArray(TypeToken<?> type) {
    return new JavaInputMapper(this, type, this.arity + 1).unwrap();
  }

  JavaInputMapper unwrap() {

    try {

      TypeToken<?> wrapped = this.javaType;

      // array types...
      if (wrapped.isArray()) {

        return unwrapArray(wrapped.getComponentType());

      }
      else if (wrapped.isSubtypeOf(Iterable.class)) {

        // return unwrapWith(Iterable.class.getMethod("iterator"));

      }
      else if (wrapped.isSubtypeOf(Iterator.class)) {

        // the component type.
        TypeToken<?> componentType = wrapped.resolveType(Iterator.class.getMethod("next").getGenericReturnType());

        // find the toArray method.
        Method method = Iterators.class.getMethod("toArray", Iterator.class, wrapped.getRawType().getClass());

        // the return type.
        TypeToken<?> returnType = TypeToken.of(Array.newInstance(componentType.getRawType(), 0).getClass());

      }

      // other stuffs ...
      else if (wrapped.isSubtypeOf(Optional.class)) {

        return unwrapWith(Optional.class.getMethod("get"));

      }
      else if (wrapped.isSubtypeOf(CompletableFuture.class)) {
        return unwrapWith(CompletableFuture.class.getMethod("get"));
      }

      else if (wrapped.isSubtypeOf(StringBuilder.class)) {
        return unwrap(TypeToken.of(String.class));
      }

      return this;

    }
    catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }
}
