package io.joss.graphql.executor;

import java.lang.reflect.Array;

import com.google.common.base.Preconditions;

import io.joss.graphql.core.binder.execution.QueryEnvironment;
import io.joss.graphql.core.doc.GQLOpType;
import io.joss.graphql.core.doc.GQLSelectedOperation;
import io.joss.graphql.core.parser.GQLException;
import io.joss.graphql.core.value.GQLObjectValue;
import io.joss.graphql.core.value.GQLValue;

/**
 * Entry point for executing GraphQL documents.
 * 
 * @author theo
 *
 */

public class GraphQLEngine
{

  private GraphQLEngineConfig app;

  /**
   * Engine from the given config.
   */

  public GraphQLEngine(GraphQLEngineConfig app)
  {
    this.app = app;
  }

  /**
   * Executes the given query.
   * 
   * @param env
   *          The query environment.
   * 
   * @param op
   *          The requested operation.
   * 
   * @param input
   *          Any input values, if provided.
   * 
   * @return
   */

  public GQLObjectValue execute(QueryEnvironment env, GQLSelectedOperation op, Object root, GQLObjectValue input)
  {

    // only support query for now.
    Preconditions.checkArgument(op.operation().type() == GQLOpType.Query);

    // run through each selection, resolve it, validate, and then build the execution plan.
    op.operation().selections();

    // generate an execution plan.
    ExecutionContext builder = new ExecutionContext(this, env, op);

    if (input != null)
    {
      builder.input(input);
    }

    Object[] ret = (Object[]) Array.newInstance(root.getClass(), 1);

    ret[0] = root;

    GraphQLOutputType type = app.type(root.getClass());

    if (type == null)
    {
      throw new GQLException(String.format("type '%s' isn't registered", root.getClass().getName()));
    }

    return builder.selections(type, ret, op.operation().selections())[0];

  }

  public GraphQLOutputType type(Class<?> type)
  {
    return this.app.type(type);
  }

}
