package io.joss.graphql.core.binder.annotatons;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value = { ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface GQLContext
{
}
