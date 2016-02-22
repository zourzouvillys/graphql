package io.joss.graphql.core.binder.annotatons;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface GQLField
{

  String name() default "";

  String description() default "";

  /**
   * Inform the scanner that this field is actually the given GraphQL named type.
   * 
   * Only scalar values are currently supported.
   * 
   */

  String type() default "";

}
