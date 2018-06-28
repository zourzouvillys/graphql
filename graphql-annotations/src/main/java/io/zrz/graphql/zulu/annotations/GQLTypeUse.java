package io.zrz.graphql.zulu.annotations;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * specifications for a type usage.
 */

@Repeatable(GQLTypeUses.class)
@Retention(RUNTIME)
@Target({ ElementType.TYPE_USE })
public @interface GQLTypeUse {

  /**
   * if this type usage is nullable or not.
   */

  boolean nullable() default true;

  /**
   * if this type usage requires a new type to be generated for GraphQL (e.g, it's a union of results that don't have a
   * common interface or already registered name) then the name is provided here.
   */

  String name() default "";

}
