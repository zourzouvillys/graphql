package io.joss.graphql.jersey.resource;

import io.joss.graphql.core.binder.annotatons.GQLArg;
import io.joss.graphql.core.binder.annotatons.GQLContext;
import io.joss.graphql.core.binder.annotatons.GQLField;
import io.joss.graphql.core.binder.annotatons.GQLNonNull;
import io.joss.graphql.core.binder.annotatons.GQLType;
import io.joss.graphql.executor.GraphQLEngine;
import io.joss.graphql.schema.__Schema;
import io.joss.graphql.schema.__Type;

@GQLType
public class SchemaQueryRoot 
{

  @GQLField
  public @GQLNonNull __Schema __schema(@GQLContext GraphQLEngine engine)
  {    
    return engine.schema();
  }

  @GQLField
  public __Type __type(@GQLContext GraphQLEngine engine, @GQLArg("name") @GQLNonNull String name)
  {
    return engine.schema(name);
  }

}
