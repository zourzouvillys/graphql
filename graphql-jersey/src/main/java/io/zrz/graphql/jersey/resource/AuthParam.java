package io.zrz.graphql.jersey.resource;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicate to JAX-RS that the parameter annotated with this should inject the parsed credentials, either from a access_token query
 * parameter, or HTTP Authorization headers.
 * 
 * @author theo
 *
 */

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AuthParam
{

}
