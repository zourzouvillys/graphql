package io.zrz.graphql.core.binder.execution.planner;

import io.zrz.graphql.core.binder.execution.QueryEnvironment;
import io.zrz.graphql.core.binder.runtime.DataContext;

/**
 * An execution planner is responsible for taking a {@link DataContext}, and returning a {@link PreparedQuery} which a
 * {@link QueryEnvironment} can then be applied to (multiple times).
 * 
 * The planning stage is used to allow pre-calculations of common expensive operations so that all queries using the same document but
 * different inputs can share the initial calculation.
 * 
 * The planner decides on the most optimal way to perform the execution. Some may decide to execute in parallel, some in serial, and others
 * a mixture of both.
 * 
 * Currently, there is a single implementation, "basic" which performs a synchronous, serial execution.
 * 
 * @author theo
 *
 */
public interface ExecutionPlanner
{

  /**
   * Perform the planning stage, returning back a prepared query.
   * 
   * @param ctx
   * @return
   */
  
  PreparedQuery plan(DataContext ctx);

}
