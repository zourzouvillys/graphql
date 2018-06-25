package io.zrz.graphql.zulu.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import io.zrz.graphql.zulu.annotations.GQLType.Kind;

/**
 * indicates that the element annotated with this is an input type.
 *
 * @author theo
 *
 */

@Retention(RUNTIME)
@Target(TYPE)
@GQLType(kind = Kind.INPUT)
public @interface GQLInputType {

  String name() default "";

}
