package io.zrz.graphql.zulu.executable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import com.google.common.reflect.TypeToken;

import io.zrz.graphql.zulu.JavaInputField;
import io.zrz.graphql.zulu.JavaOutputField;
import io.zrz.graphql.zulu.annotations.GQLNullable;
import io.zrz.graphql.zulu.annotations.GQLType;
import io.zrz.graphql.zulu.annotations.GQLType.Kind;
import io.zrz.graphql.zulu.annotations.GQLTypeUse;

public class JavaExecutableUtils {

  public static Method getDeclaredMethod(final Method myMethod) {
    final Class<?> declaringClass = myMethod.getDeclaringClass();
    try {

      final Class<?> superClass = declaringClass.getSuperclass();

      if (superClass != null) {
        if (Modifier.isPublic(superClass.getModifiers()))
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

  /**
   *
   */

  public static Stream<Method> getMethods(final Method myMethod) {

    return TypeToken
        .of(myMethod.getDeclaringClass())
        .getTypes()
        .stream()
        .flatMap(st -> Arrays.stream(st.getRawType().getMethods()))
        .filter(st -> st.getName().equals(myMethod.getName()))
    // .filter(st -> st.getParameterTypes().equals(myMethod.getParameterTypes()))
    ;

  }

  /**
   * @return
   *
   */

  static Stream<GQLTypeUse> getMethodReturnTypeUseAnnotations(final JavaOutputField field) {

    final @Nullable Method method = field.origin()
        .filter(Method.class::isInstance)
        .map(m -> (@Nullable Method) m)
        .orElse(null);

    if (method == null) {
      return Stream.empty();
    }

    return getMethods(method)
        .map(a -> a.getAnnotatedReturnType().getAnnotationsByType(GQLTypeUse.class))
        .flatMap(a -> Arrays.stream(a));

  }

  /**
   * makes an array of the given length for input data.
   *
   * @param param
   * @param length
   * @return
   */
  public static Object[] makeArray(final ExecutableInput param, final int length) {
    return (Object[]) Array.newInstance(param.fieldType().javaType().getRawType(), length);
  }

  public static List<String> enumValuesFrom(final Class<?> rawType) {

    final Class<? extends Enum<?>> enumType = (Class<? extends Enum<?>>) rawType;

    return Arrays.stream(enumType.getEnumConstants()).map(e -> e.name()).collect(Collectors.toList());
  }

}
