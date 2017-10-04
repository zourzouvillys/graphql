package io.zrz.graphql.jersey.resource;

import io.zrz.graphql.core.binder.annotatons.GQLArg;
import io.zrz.graphql.core.binder.annotatons.GQLContext;
import io.zrz.graphql.core.binder.annotatons.GQLField;
import io.zrz.graphql.core.binder.annotatons.GQLNonNull;
import io.zrz.graphql.executor.GraphQLEngine;
import io.zrz.graphql.schema.__Schema;
import io.zrz.graphql.schema.__Type;

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
