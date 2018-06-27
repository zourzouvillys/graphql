package io.zrz.graphql.zulu;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Method;
import java.util.Objects;

import io.zrz.zulu.values.ZValue;

public class ZuluUtils {

  public static MethodHandle unreflect(final Method m) {
    return unreflect(MethodHandles.publicLookup(), m);
  }

  public static MethodHandle unreflect(final Lookup lookup, final Method m) {
    try {
      return lookup.unreflect(m);
    }
    catch (final IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  public static void invokeExact(final MethodHandle handle, final Object builder, final ZValue value) {
    try {
      handle.invoke(Objects.requireNonNull(builder), Objects.requireNonNull(value));
    }
    catch (final Throwable e) {
      throw new RuntimeException(e);
    }
  }

  public static MethodHandle castTo(final MethodHandle target, final Class<?> type) {
    return target.asType(target.type().changeReturnType(type));
  }

  public static MethodHandle unreflect(final Lookup lookup, final Class<?> klass, final String methodName, final Class<?>... args) {
    try {
      return unreflect(lookup, klass.getDeclaredMethod(methodName, args));
    }
    catch (NoSuchMethodException | SecurityException e) {
      throw new RuntimeException(e);
    }
  }

  @FunctionalInterface
  public interface SneakySupplier<R> {

    R run() throws Throwable;

  }

  public static <R> R sneakyThrows(final SneakySupplier<R> lambda) {
    try {
      return lambda.run();
    }
    catch (final RuntimeException e) {
      throw e;
    }
    catch (final Throwable e) {
      throw new RuntimeException(e);
    }
  }

  public static <T> MethodHandle getMethodHandle(final Lookup lookup, final Class<T> receiver, final String methodName, final Class<?>... parameterTypes) {
    return unreflect(lookup, getMethod(receiver, methodName, parameterTypes));
  }

  public static <T> MethodHandle getMethodHandle(final Class<T> receiver, final String methodName, final Class<?>... parameterTypes) {
    return unreflect(MethodHandles.publicLookup(), getMethod(receiver, methodName, parameterTypes));
  }

  public static <T> Method getMethod(final Class<T> receiver, final String methodName, final Class<?>... parameterTypes) {
    try {
      return receiver.getMethod(methodName, parameterTypes);
    }
    catch (NoSuchMethodException | SecurityException e) {
      throw new RuntimeException(e);
    }
  }

}
