package io.zrz.graphql.zulu.executable;

import java.lang.annotation.Annotation;
import java.util.Optional;

import com.google.common.reflect.TypeToken;

import io.zrz.graphql.zulu.annotations.GQLType;
import io.zrz.graphql.zulu.annotations.GQLType.Kind;

public class JavaExecutableUtils {

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
   * merges two fields declared for a type to find the winning one.
   *
   * @param receiver
   * @param a
   * @param b
   * @return
   */

  public static ExecutableOutputField merge(final ExecutableReceiverType receiver, final ExecutableOutputField a, final ExecutableOutputField b) {
    // simplest solution for now: return the first.
    return a;
  }

}
