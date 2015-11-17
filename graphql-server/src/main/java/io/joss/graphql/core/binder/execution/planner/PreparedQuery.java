package io.joss.graphql.core.binder.execution.planner;

import java.util.function.Consumer;

import io.joss.graphql.core.binder.execution.QueryEnvironment;
import io.joss.graphql.core.value.GQLValue;

/**
 * A query that has been prepared for execution. all that is left to do it provide an actual query environment (which includes input
 * parameters, runtime context information, etc).
 * 
 * @author theo
 *
 */

public interface PreparedQuery
{

  /**
   * Execute an instance of this query.
   */

  void execute(Object root, QueryEnvironment env, Consumer<GQLValue> result);

}
