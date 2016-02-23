package io.joss.graphql.schema;

import com.google.common.base.Preconditions;

import io.joss.graphql.core.binder.annotatons.GQLContext;
import io.joss.graphql.core.binder.annotatons.GQLField;
import io.joss.graphql.core.binder.annotatons.GQLType;
import io.joss.graphql.core.types.GQLTypeReference;
import io.joss.graphql.executor.GraphQLEngine;
import io.joss.graphql.executor.GraphQLInputType.Field;
import io.joss.graphql.executor.GraphQLOutputType.Arg;

@GQLType
public class __InputValue
{

  private Arg arg;
  private Field field;

  __InputValue(Arg arg)
  {
    this.arg = arg;
  }

  __InputValue(Field arg)
  {
    this.field = arg;
  }

  @GQLField
  public String name()
  {
    if (this.field != null)
    {
      return this.field.name();
    }
    return arg.name();
  }

  @GQLField
  public String description()
  {
    if (this.field != null)
    {
      return this.field.description();
    }
    return arg.description();
  }

  @GQLField
  public __Type type(@GQLContext GraphQLEngine engine)
  {
    if (this.field != null)
    {
      return engine.type(this.field.returnType());
    }
    Preconditions.checkNotNull(arg.type());
    return engine.type(arg.type());
  }

  @GQLField
  public String defaultValue()
  {
    return null;
  }

}
