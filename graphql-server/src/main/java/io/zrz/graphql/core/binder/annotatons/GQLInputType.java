package io.zrz.graphql.core.binder.annotatons;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.zrz.graphql.core.decl.GQLObjectTypeDeclaration;
import io.zrz.graphql.core.decl.GQLTypeDeclaration;

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
