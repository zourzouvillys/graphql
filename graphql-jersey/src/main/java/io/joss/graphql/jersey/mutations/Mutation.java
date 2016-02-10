package io.joss.graphql.jersey.mutations;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Mutation
{

  String name() default "";

  String description() default "";
  

}