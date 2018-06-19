package io.zrz.graphql.zulu.annotations;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * automatically wraps the resulting collection into a relay connection. each result will be an edge, and the cursor
 * will be taken from a field on the value.
 * 
 * the value returned from the method is expected to contain all of the entries in this connection. the execution engine
 * will request the specific edges, and generate the pageInfo.
 * 
 * @author theo
 *
 */
@Retention(RUNTIME)
@Target({ ElementType.TYPE_USE })
public @interface RelayAutoConnection {

  /**
   * the field to use on the return type which will generate the cursor value. it must be a method which returns a
   * string, and does not need to be exposed in the zulu model.
   */

  String cursorMethod();

  /**
   * the name to give the connection type which is generated.
   */

  String connectionTypeName() default "*Connection";

  /**
   * the name to give the connection type which is generated.
   */

  String edgeTypeName() default "*Edge";

}
