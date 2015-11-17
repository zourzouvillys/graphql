package io.joss.graphql.core.binder.annotatons;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE_USE)
@Retention(RetentionPolicy.RUNTIME)
public @interface GQLTypeUse
{

  /**
   * a GraphQL concrete type reference. this means you can't have a [] or ! on it. It must just be the simple type name.
   * 
   * @return
   */

  String value();

}
