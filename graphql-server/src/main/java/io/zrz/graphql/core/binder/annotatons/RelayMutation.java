package io.zrz.graphql.core.binder.annotatons;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the method annotated with this is a mutation and should implement the relay mutation spec (single $input), return with
 * {name}Paylaod, and echoes clientMutationId.
 * 
 * @author theo
 *
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RelayMutation
{
  String inputTypePrefix() default "";
}
