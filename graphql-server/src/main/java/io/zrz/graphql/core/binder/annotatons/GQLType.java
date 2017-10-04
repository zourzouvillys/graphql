package io.zrz.graphql.core.binder.annotatons;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.zrz.graphql.core.decl.GQLObjectTypeDeclaration;
import io.zrz.graphql.core.decl.GQLTypeDeclaration;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface GQLType
{

  /**
   * Overrides the name generation.
   */
  
  String name() default "";
  
  /**
   * A description on the type itself.
   */
  
  String description() default "";
  
  /**
   * IF you want fields that are public and contain references to other GQL types to be included by defaul withotu explicitly needing to
   * annotation @GQLField, then set this to true.
   */
  
  boolean autoField() default false;

  /**
   * The type of this declaration.
   * 
   * @return
   */

  Class<? extends GQLTypeDeclaration> type() default GQLObjectTypeDeclaration.class;
 
}
