package io.joss.graphql.core.binder.annotatons;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.joss.graphql.core.decl.GQLTypeDeclaration;
import io.joss.graphql.core.decl.GQLObjectTypeDeclaration;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface GQLInputType
{

  /**
   * Overrides the name generation.
   */

  String name() default "";

  /**
   * A description on the type itself.
   */

  String description() default "";

}
