package io.zrz.graphql.zulu.annotations;

import static java.lang.annotation.ElementType.PACKAGE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * indicates if the type (or types) enclosed in the element this is annotated on should be scanned automatically.
 * 
 * @author theo
 *
 */

@Retention(RUNTIME)
@Target({ TYPE, PACKAGE })
public @interface GQLAutoScan {

  boolean value() default true;

}
