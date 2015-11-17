package io.joss.graphql.core.binder.execution.pipeline;

import io.joss.graphql.core.binder.execution.QueryEnvironment;

public class ProcessingPipeline
{

  private QueryEnvironment env;

  public ProcessingPipeline(QueryEnvironment env)
  {
    this.env = env;
  }

  public QueryEnvironment env()
  {
    return this.env;
  }

}
