package io.zrz.graphql.zulu.annotations;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * indicates that the annotation it is on is a GQLType
 *
 * @author theo
 *
 */

@Retention(RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
@Inherited
public @interface GQLType {

  Kind kind();

  public enum Kind {
    SCALAR,
    OBJECT,
    INTERFACE,
    UNION,
    ENUM,
    INPUT
  }

}
