package io.zrz.graphql.zulu.annotations;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * spreads the parameter annotated with this to the parameter set exported by this method.
 * 
 * the name of the parameter is ignored, and not exposed.
 * 
 * this may be used on multiple parameters of the same method, however it will fail if there are duplicate names in the
 * resulting type.
 * 
 * @author theo
 *
 */

@Retention(RUNTIME)
@Target({ PARAMETER })
public @interface GQLSpread {

  boolean value() default true;

}
