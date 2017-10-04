package io.zrz.graphql.executor.fixtures;

import io.zrz.graphql.core.binder.annotatons.GQLField;

public class BaseTest2Root
{

  public String notAField()
  {
    return "ok";
  }

  @GQLField
  public String baseField()
  {
    return "ok";
  }
  
  @GQLField
  public static String[] staticChild(Test2Root[] roots)
  {
    return new String[roots.length];
  }


  // note: no GQLField.
  protected static String[] protectedField(Test2Root[] roots)
  {
    return new String[roots.length];
  }

  // note: no GQLField.
  private static String[] privateField(Test2Root[] roots)
  {
    return new String[roots.length];
  }

}
