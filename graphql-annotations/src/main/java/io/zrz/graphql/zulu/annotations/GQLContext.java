package io.zrz.graphql.zulu.annotations;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * indicates the value for this parameter should come from the context.
 * 
 * @author theo
 *
 */

@Retention(RUNTIME)
@Target({ PARAMETER })
public @interface GQLContext {

}
