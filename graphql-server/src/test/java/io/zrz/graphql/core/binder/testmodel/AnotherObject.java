package io.zrz.graphql.core.binder.testmodel;

import io.zrz.graphql.core.binder.annotatons.GQLField;
import io.zrz.graphql.core.binder.annotatons.GQLType;

@GQLType
public class AnotherObject
{

  @GQLField
  public String getId()
  {
    return "xxx";
  }

}
