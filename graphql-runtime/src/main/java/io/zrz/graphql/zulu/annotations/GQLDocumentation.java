package io.zrz.graphql.zulu.annotations;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * indicates that the specified parameter, method, or field is a GQL field.
 * 
 * on a parameter, configures the input field. on a method, configures the output one it represents.
 * 
 * @author theo
 *
 */

@Retention(RUNTIME)
@Target({ ElementType.PARAMETER, ElementType.METHOD, ElementType.FIELD, ElementType.TYPE })
public @interface GQLDocumentation {

  /**
   * defines the name of the field.
   */

  String value() default "";

}
