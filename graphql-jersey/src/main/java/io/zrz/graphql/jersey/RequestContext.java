package io.zrz.graphql.jersey;


import io.zrz.graphql.core.binder.execution.QueryEnvironment;
import io.zrz.graphql.core.doc.GQLSelectedOperation;
import io.zrz.graphql.core.value.GQLObjectValue;
import io.zrz.graphql.core.value.GQLValue;

/**
 * Provides scope for dispatching queries, mutations, and subscriptions from the client.
 * 
 * These must be short lived. They can potentially hold resources open, so don't hold open for a subscription, or other long term execution.
 * 
 * @author theo
 *
 */

public class RequestContext implements AutoCloseable
{

  private GraphQLEngineCore frontend;

  public RequestContext(GraphQLEngineCore frontend)
  {
    this.frontend = frontend;
  }


  /**
   * Execute the given operation.
   * 
   * @param query
   *          The selected operation. May be a mutation or query.
   * 
   * @param input
   *          The input parameters.
   * 
   * @return
   */

  public GQLObjectValue query(GQLSelectedOperation op, GQLValue input)
  {

    QueryEnvironment env = QueryEnvironment.builder()
        .context(GraphQLEngineCore.class, frontend)
        .context(RequestContext.class, this)
        .build();

    return frontend.execute(env, op, input);

  }

  /**
   * called for every single instance, once the request has been processed and the response sent.
   */

  @Override
  public void close()
  {
  }

}