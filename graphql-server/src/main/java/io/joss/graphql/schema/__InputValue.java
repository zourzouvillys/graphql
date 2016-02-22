package io.joss.graphql.schema;

import io.joss.graphql.core.binder.annotatons.GQLField;
import io.joss.graphql.core.binder.annotatons.GQLType;
import io.joss.graphql.core.types.GQLTypeReference;
import io.joss.graphql.executor.GraphQLOutputType.Arg;

@GQLType
public class __InputValue
{

  private Arg arg;

  __InputValue(Arg arg)
  {
    this.arg = arg;
  }

  @GQLField
  public String name()
  {
    return arg.name();
  }

  @GQLField
  public String description()
  {
    return arg.name();
  }

  @GQLField
  public __Type type()
  {
    return new __Type("String");
  }

  @GQLField
  public String defaultValue()
  {
    return null;
  }

}
