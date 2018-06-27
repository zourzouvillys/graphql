package io.zrz.graphql.zulu.annotations;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * indicates that by default, type usages are not nullable.
 */

@Retention(RUNTIME)
@Target({ ElementType.PACKAGE, ElementType.TYPE })
@GQLTypeUse(nullable = false)
public @interface GQLNotNullableByDefault {

}
