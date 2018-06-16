package io.zrz.graphql.zulu.binding;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.stream.Stream;

import com.google.common.reflect.TypeParameter;
import com.google.common.reflect.TypeToken;

public class JavaBindingUtils {

  public static Stream<Method> listMethods(TypeToken<?> klass) {
    return Arrays.stream(klass.getRawType().getDeclaredMethods())
        .filter(JavaBindingUtils::matches);
  }

  private static boolean matches(Method method) {

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

  public static String autoTypeName(TypeToken<?> typeToken) {
    return typeToken.getRawType().getSimpleName();
  }

  /**
   * 
   */

  public static String normalizeTypeName(String typeName) {
    return typeName.toLowerCase();
  }

  /**
   * for diagnostics.
   * 
   * @param method
   * @return
   */

  public static String toString(Method method) {

    StringBuilder sb = new StringBuilder();

    sb.append(method.getDeclaringClass().getPackage().getName());
    sb.append(".");
    sb.append(method.getDeclaringClass().getSimpleName());
    sb.append("::");
    sb.append(method.getName());

    return sb.toString();
  }

  public static String toString(AnnotatedElement origin) {
    if (origin instanceof Method) {
      Method method = (Method) origin;
      return JavaBindingUtils.toString(method);
    }
    return origin.toString();
  }

  @SuppressWarnings("serial")
  public static <T> TypeToken<?> resolveIterableType(TypeToken<T> returnType) {
    return new TypeToken<Iterable<T>>() {}
        .where(new TypeParameter<T>() {}, returnType);

  }

}
