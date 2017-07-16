package io.joss.graphql.core.binder.annotatons;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.joss.graphql.core.decl.GQLTypeDeclaration;
import io.joss.graphql.core.decl.GQLObjectTypeDeclaration;

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
