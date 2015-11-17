package io.jgql.generator.java.modeltest;

import io.joss.graphql.core.binder.annotatons.GQLArg;
import io.joss.graphql.core.binder.annotatons.GQLType;

@GQLType(autoField = true)
public class PhoneNumber implements TestNode
{

  public String getId()
  {
    return null;
  }

  public String getName()
  {
    return null;
  }

  public String getDialstring(@GQLArg("format") String numberFormat)
  {
    return null;
  }

  public String getCallerName()
  {
    return null;
  }

}
