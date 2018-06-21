package io.zrz.graphql.zulu.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * indicates that the specified type is a GQL object type
 *
 * @author theo
 *
 */

@Retention(RUNTIME)
@Target(TYPE)
public @interface GQLObjectType {

  String name() default "";

}
