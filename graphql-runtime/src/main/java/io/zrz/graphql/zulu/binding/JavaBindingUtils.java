package io.zrz.graphql.zulu.binding;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.stream.Stream;

import com.google.common.reflect.TypeParameter;
import com.google.common.reflect.TypeToken;

public class JavaBindingUtils {

  /**
   *
   * @param klass
   * @return
   */

  public static Stream<Method> listMethods(final TypeToken<?> klass) {
    return Arrays.stream(klass.getRawType().getDeclaredMethods())
        .filter(JavaBindingUtils::matches);
  }

  private static boolean matches(final Method method) {

    if (!Modifier.isPublic(method.getModifiers())) {
      return false;
    }

    return true;

  }

  /**
   * attempt to generate a zulu name for this type token.
   *
   * @param typeToken
   * @return
   */

  public static String autoTypeName(final TypeToken<?> typeToken) {
    return typeToken.getRawType().getSimpleName();
  }

  /**
   *
   */

  public static String normalizeTypeName(final String typeName) {
    return typeName.toLowerCase();
  }

  /**
   * for diagnostics.
   *
   * @param method
   * @return
   */

  public static String toString(final Method method) {

    final StringBuilder sb = new StringBuilder();

    sb.append(method.getDeclaringClass().getPackage().getName());
    sb.append(".");
    sb.append(method.getDeclaringClass().getSimpleName());
    sb.append("::");
    sb.append(method.getName());

    return sb.toString();
  }

  public static String toString(final AnnotatedElement origin) {
    if (origin instanceof Method) {
      final Method method = (Method) origin;
      return JavaBindingUtils.toString(method);
    }
    return origin.toString();
  }

  @SuppressWarnings("serial")
  public static <T> TypeToken<?> resolveIterableType(final TypeToken<T> returnType) {
    return new TypeToken<Iterable<T>>() {}
        .where(new TypeParameter<T>() {}, returnType);

  }

}
