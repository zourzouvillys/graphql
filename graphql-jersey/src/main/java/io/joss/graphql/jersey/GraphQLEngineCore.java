package io.joss.graphql.jersey;

import io.joss.graphql.core.binder.execution.QueryEnvironment;
import io.joss.graphql.core.doc.GQLSelectedOperation;
import io.joss.graphql.core.value.GQLObjectValue;
import io.joss.graphql.core.value.GQLValue;
import io.joss.graphql.jersey.auth.RegistryAuthValue;

public class GraphQLEngineCore
{
  
  private RegistryGql gql;
  private Object queryRoot;

  public GraphQLEngineCore(Object queryRoot)
  {
    this.queryRoot = queryRoot;
    
    MutationRegistry registry = new MutationRegistry();    
    // registry.register(new TaskMutations());
    this.gql = new RegistryGql(queryRoot.getClass(), registry);
    
  }

  /**
   * Opens a request context for a request of the given type and with provided auth token value.
   * @param authValue 
   */

  public RequestContext open(RequestType type, RegistryAuthValue authValue)
  {
    return new RequestContext(this);
  }

  /**
   * @return The GraphQL schema in text form.
   */

  public String schema()
  {
    return gql.schema();
  }

  /**
   * 
   * @param env
   * @param op
   * @param input
   * @return
   */
  
  public GQLObjectValue execute(QueryEnvironment env, GQLSelectedOperation op, GQLValue input)
  {
    return gql.query(this.queryRoot, op, env, input);
  }


}
