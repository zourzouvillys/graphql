package io.zrz.graphql.zulu;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Method;
import java.util.Objects;

import io.zrz.zulu.values.ZValue;

public class ZuluUtils {

  public static MethodHandle unreflect(Lookup lookup, Method m) {
    try {
      return lookup.unreflect(m);
    }
    catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  public static void invokeExact(MethodHandle handle, Object builder, ZValue value) {
    try {
      handle.invoke(Objects.requireNonNull(builder), Objects.requireNonNull(value));
    }
    catch (Throwable e) {
      throw new RuntimeException(e);
    }

  }

  public static MethodHandle castTo(MethodHandle target, Class<?> type) {
    return target.asType(target.type().changeReturnType(type));
  }

}
