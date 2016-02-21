package io.joss.graphql.executor.fixtures;

import io.joss.graphql.core.binder.annotatons.GQLArg;
import io.joss.graphql.core.binder.annotatons.GQLField;
import io.joss.graphql.core.binder.annotatons.GQLType;

@GQLType
public class Test3Root
{

  @GQLField
  public String stringArg(@GQLArg("arg1") String arg1)
  {
    return arg1;
  }

  @GQLField
  public int intArg(@GQLArg("arg1") int arg1)
  {
    return arg1;
  }

}
