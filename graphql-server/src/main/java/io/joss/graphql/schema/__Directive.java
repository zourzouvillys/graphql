package io.joss.graphql.schema;

import io.joss.graphql.core.binder.annotatons.GQLField;
import io.joss.graphql.core.binder.annotatons.GQLType;

@GQLType(name = "__Directive")
public class __Directive
{

  @GQLField
  public String name()
  {
    return null;
  }

  @GQLField
  public String description()
  {
    return null;
  }

  @GQLField
  public __InputValue[] args()
  {
    return new __InputValue[0];
  }

  @GQLField
  public Boolean onOperation()
  {
    return false;
  }

  @GQLField
  public Boolean onFragment()
  {
    return false;
  }

  @GQLField
  public Boolean onField()
  {
    return false;
  }

}
