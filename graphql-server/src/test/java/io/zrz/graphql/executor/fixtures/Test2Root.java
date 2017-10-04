package io.zrz.graphql.executor.fixtures;

import io.zrz.graphql.core.binder.annotatons.GQLField;
import io.zrz.graphql.core.binder.annotatons.GQLType;

/**
 * Override the base field.
 * 
 * Overriding parents results in the final overriden @GQLField being used.  All others are ignored.
 * 
 * @author theo
 *
 */

@GQLType
public class Test2Root extends BaseTest2Root
{

  @GQLField
  public String id()
  {
    return "x";
  }

  /**
   * Override the parent one.
   * 
   * @param roots
   * @return
   */

  @GQLField
  public static String[] staticChild(Test2Root[] roots)
  {
    return new String[roots.length];
  }

  @GQLField
  public static String[] protectedField(Test2Root[] roots)
  {
    return new String[roots.length];
  }

  @GQLField
  public static String[] privateField(Test2Root[] roots)
  {
    return new String[roots.length];
  }

}
