package io.zrz.graphql.zulu.annotations;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * specifications for a type usage.
 */

@Retention(RUNTIME)
@Target({ ElementType.TYPE_USE })
@GQLTypeUse(nullable = true)
public @interface GQLNull {

}
