package io.zrz.graphql.core.binder.annotatons;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indication of the interfaces that this class implements, without having to actually inherit.
 * 
 * @author Theo Zourzouvillys
 *
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface GQLImplements
{

  /**
   * The GQL types (annotated with @GQLType) this class extends.
   */

  Class<?>[] classes() default {};

  /**
   * The GQL types (named) this class extends.
   */

  String[] names() default {};

}
