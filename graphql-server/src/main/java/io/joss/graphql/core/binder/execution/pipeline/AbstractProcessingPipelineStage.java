package io.joss.graphql.core.binder.execution.pipeline;

import io.joss.graphql.core.binder.runtime.DataContext;

public abstract class AbstractProcessingPipelineStage implements ProcessingPipelineStage
{

  protected final DataContext ctx;
  protected final ProcessingPipeline pipeline;

  protected AbstractProcessingPipelineStage(ProcessingPipeline pipeline, DataContext ctx)
  {
    this.ctx = ctx;
    this.pipeline = pipeline;
  }

  @Override
  public DataContext context()
  {
    return ctx;
  }

  @Override
  public ProcessingPipeline pipeline()
  {
    return pipeline;
  }

}
