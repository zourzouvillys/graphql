package io.zrz.graphql.zulu.annotations;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * specifications for a type usage.
 */

@Retention(RUNTIME)
@Target({ ElementType.METHOD })
@GQLTypeUse(nullable = false)
public @interface GQLPath {
  String[] value();
}
