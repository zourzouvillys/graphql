package io.zrz.graphql.zulu.annotations;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * indicates a parameter may be null
 */

@Retention(RUNTIME)
@Target({ ElementType.PARAMETER })
@GQLTypeUse(nullable = true)
public @interface GQLNullable {

}
