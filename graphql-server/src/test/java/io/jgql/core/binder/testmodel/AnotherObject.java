package io.jgql.core.binder.testmodel;

import io.joss.graphql.core.binder.annotatons.GQLField;
import io.joss.graphql.core.binder.annotatons.GQLType;

@GQLType
public class AnotherObject
{

  @GQLField
  public String getId()
  {
    return "xxx";
  }

}
