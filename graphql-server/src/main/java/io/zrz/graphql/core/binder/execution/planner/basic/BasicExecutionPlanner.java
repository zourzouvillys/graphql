package io.zrz.graphql.core.binder.execution.planner.basic;

import io.zrz.graphql.core.binder.BindingProvider;
import io.zrz.graphql.core.binder.execution.planner.ExecutionPlanner;
import io.zrz.graphql.core.binder.execution.planner.PreparedQuery;
import io.zrz.graphql.core.binder.runtime.DataContext;

public class BasicExecutionPlanner implements ExecutionPlanner
{

  private BindingProvider binder;

  public BasicExecutionPlanner(BindingProvider binder)
  {
    this.binder = binder;
  }

  @Override
  public PreparedQuery plan(DataContext ctx)
  {
    return new BasicPreparedQuery(binder, ctx);
  }

}
