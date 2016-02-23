package io.joss.graphql.schema;

import io.joss.graphql.core.binder.annotatons.GQLContext;
import io.joss.graphql.core.binder.annotatons.GQLField;
import io.joss.graphql.core.binder.annotatons.GQLType;
import io.joss.graphql.executor.GraphQLEngine;
import io.joss.graphql.executor.GraphQLInputType;
import io.joss.graphql.executor.GraphQLOutputType;

@GQLType(name = "__Field")
public class __Field
{

  private GraphQLOutputType.Field field;

  public __Field(GraphQLOutputType.Field f)
  {
    this.field = f;
  }

  @GQLField
  public String name()
  {
    return this.field.name();
  }

  @GQLField
  public String description()
  {
    return field.description();
  }

  @GQLField
  public __InputValue[] args()
  {
    if (this.field != null)
    {
      return field.args().stream().map(__InputValue::new).toArray(__InputValue[]::new);
    }
    return new __InputValue[0];
  }

  @GQLField
  public Boolean isDeprecated()
  {
    return false;
  }

  @GQLField
  public String deprecationReason()
  {
    return null;
  }

  @GQLField
  public __Type type(@GQLContext GraphQLEngine engine)
  {
    return __Type.type(engine, field.returnType());
  }

}
