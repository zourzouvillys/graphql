package io.joss.graphql.executor.fixtures;

import io.joss.graphql.core.binder.annotatons.GQLArg;
import io.joss.graphql.core.binder.annotatons.GQLContext;
import io.joss.graphql.core.binder.annotatons.GQLDefaultValue;
import io.joss.graphql.core.binder.annotatons.GQLField;
import io.joss.graphql.core.binder.annotatons.GQLType;
import io.joss.graphql.core.binder.runtime.DataContext;

@GQLType
public class Test1Root
{

  @GQLField
  public String id()
  {
    return "x";
  }

  @GQLField
  public int value(@GQLArg("in") @GQLDefaultValue("hello") String in)
  {
    return 1;
  }

  @GQLField
  public Test2Root child()
  {
    return new Test2Root();
  }

  @GQLField
  public Test2Root[] children()
  {
    return new Test2Root[] { new Test2Root() };
  }

  @GQLField
  public String nullableChild(@GQLContext DataContext ctx)
  {
    return null;
  }
  
  /**
   * returns an array the exact same size as the input.
   */

  @GQLField(name = "simplelist")
  public static String[][] staticChild(Test1Root[] roots)
  {

    String[][] ret = new String[roots.length][3];

    for (int i = 0; i < roots.length; ++i)
    {
      ret[i][0] = "hello1";
      ret[i][1] = null;
      ret[i][2] = "hello3";
    }

    return ret;
    
  }

}
