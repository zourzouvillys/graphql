package io.joss.graphql.core.binder.execution.planner.basic;

import java.util.function.Consumer;

import io.joss.graphql.core.binder.BindingProvider;
import io.joss.graphql.core.binder.execution.QueryEnvironment;
import io.joss.graphql.core.binder.execution.planner.PreparedQuery;
import io.joss.graphql.core.binder.runtime.DataContext;
import io.joss.graphql.core.value.GQLValue;

public class BasicPreparedQuery implements PreparedQuery
{

  private BindingProvider binder;
  private DataContext ctx;

  public BasicPreparedQuery(BindingProvider binder, DataContext ctx)
  {
    this.binder = binder;
    this.ctx = ctx;
  }

  @Override
  public void execute(Object root, QueryEnvironment env, Consumer<GQLValue> result)
  {
    new BasicExecution(binder, env, ctx, result).execute(root);
  }

}
