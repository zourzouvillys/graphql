package io.zrz.graphql.zulu.executable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

import org.eclipse.jdt.annotation.NonNull;

import com.google.common.reflect.TypeToken;

import io.zrz.graphql.zulu.JavaInputField;
import io.zrz.graphql.zulu.JavaOutputField;
import io.zrz.graphql.zulu.annotations.GQLNullable;
import io.zrz.graphql.zulu.annotations.GQLType;
import io.zrz.graphql.zulu.annotations.GQLType.Kind;

public class JavaExecutableUtils {

  public static Method getDeclaredMethod(final Method myMethod) {
    final Class<?> declaringClass = myMethod.getDeclaringClass();
    try {

      final Class<?> superClass = declaringClass.getSuperclass();
      if (superClass != null) {
        return superClass.getMethod(myMethod.getName(), myMethod.getParameterTypes());
      }

    }
    catch (final NoSuchMethodException e) {
      // ignore...
    }

    return TypeToken
        .of(declaringClass)
        .getTypes()
        .stream()
        .filter(tok -> !tok.getRawType().equals(declaringClass))
        .flatMap(st -> Arrays.stream(st.getRawType().getMethods()))
        .filter(st -> st.getName().equals(myMethod.getName()))
        // .filter(st -> st.getParameterTypes().equals(myMethod.getParameterTypes()))
        .reduce((a, b) -> {

          if (a.getDeclaringClass().isAssignableFrom(b.getDeclaringClass())) {
            return a;
          }

          return b;

        })
        .orElse(myMethod);

  }

  public static class JavaTypeConfig {

    private final Kind kind;

    JavaTypeConfig(final Kind kind) {
      this.kind = kind;
    }

  }

  /**
   * scans the type to see if it should be exposed as a GRaphQL type, and if so what kind.
   *
   * @param javaType
   */

  public static Optional<GQLType.Kind> getType(final TypeToken<?> javaType) {

    for (final Annotation ant : javaType.getRawType().getAnnotations()) {

      if (ant.annotationType().isAnnotationPresent(GQLType.class)) {

        final GQLType gqlType = ant.annotationType().getAnnotation(GQLType.class);

        return Optional.of(gqlType.kind());

      }

    }

    return Optional.empty();

  }

  /**
   * when there are two fields with the same name, we need to chose one which will be the one we export.
   *
   * this happens with inheritance.
   *
   * @param receiver
   * @param a
   * @param b
   *
   * @return
   */

  public static ExecutableOutputField merge(final ExecutableReceiverType receiver, final ExecutableOutputField a, final ExecutableOutputField b) {
    if (a.fieldType().javaType().isSubtypeOf(b.fieldType().javaType())) {
      return a;
    }
    return b;
  }

  public static JavaOutputField merge(final ExecutableReceiverType receiver, final JavaOutputField a, final JavaOutputField b) {

    final Method am = (@NonNull Method) a.origin().get();
    final Method bm = (@NonNull Method) b.origin().get();

    if (am.getDeclaringClass().isAssignableFrom(bm.getDeclaringClass())) {
      return b;
    }

    return a;

  }

  public static boolean isNullableInput(final JavaInputField spec) {

    if (spec.annotation(GQLNullable.class).isPresent()) {
      return true;
    }
    else if (spec.inputType().getRawType().equals(Optional.class)) {
      return true;
    }

    return false;
  }

}
