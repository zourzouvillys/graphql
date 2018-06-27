package io.zrz.graphql.zulu.annotations;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * indicates that by default all type uses are nullable unless otherwise specified. this is the default.
 */

@Retention(RUNTIME)
@Target({ ElementType.PACKAGE, ElementType.TYPE })
@GQLTypeUse(nullable = true)
public @interface GQLNullableByDefault {

}
