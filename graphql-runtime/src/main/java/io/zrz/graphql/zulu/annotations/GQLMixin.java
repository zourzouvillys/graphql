package io.zrz.graphql.zulu.annotations;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * when used on an extends/implements type use with a java type which is not exposed as an interface or type, will
 * include methods from that base as if they were defined locally.
 * 
 * use of this will not explicitly expose the mixed in supertype, and must either be manually exposed or the 'disclose'
 * value set to true.
 * 
 * @author theo
 *
 */
@Retention(RUNTIME)
@Target({ ElementType.TYPE_USE })
public @interface GQLMixin {

  /**
   * if true, the used type will be disclosed as an interface of the supertype.
   * 
   * if set to false, the relationship will not be exposed in the GraphQL model.
   * 
   * it is an error to set this to true for a type which is registered as another GraphQL type such as a OUTPUT, UNION,
   * OR ENUM type.
   * 
   */

  boolean disclose() default false;

}
