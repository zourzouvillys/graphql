package io.joss.graphql.jersey.resource;

import io.joss.graphql.core.binder.annotatons.GQLArg;
import io.joss.graphql.core.binder.annotatons.GQLField;
import io.joss.graphql.core.binder.annotatons.GQLNonNull;
import io.joss.graphql.core.binder.annotatons.GQLType;
import io.joss.graphql.executor.GraphQLEngine;
import io.joss.graphql.schema.__Type;
import io.joss.graphql.schema.__Schema;

@GQLType
public class SchemaQueryRoot 
{

  private GraphQLEngine core;

  public SchemaQueryRoot(GraphQLEngine core)
  {
    this.core = core;
  }

  @GQLField
  public @GQLNonNull __Schema __schema()
  {
    return this.core.schema();
  }

  @GQLField
  public __Type __type(@GQLArg("name") @GQLNonNull String name)
  {
    return this.core.schema(name);
  }

}
