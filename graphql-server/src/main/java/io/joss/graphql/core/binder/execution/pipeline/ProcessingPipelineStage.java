package io.joss.graphql.core.binder.execution.pipeline;

import java.io.PrintStream;
import java.util.function.Consumer;

import io.joss.graphql.core.binder.runtime.DataContext;
import io.joss.graphql.core.value.GQLValue;

/**
 * An instance of a processing pipeline stage that runs within the context of DataContext/QueryEnvironment pair.
 * 
 * @author theo
 *
 */

public interface ProcessingPipelineStage
{

  /**
   * The datacontext this pipeline stage is for.
   */

  DataContext context();

  /**
   * The query environment/pipeline instance this stage is for.
   */

  ProcessingPipeline pipeline();

  /**
   * Provides an instance to this pipeline for processing.
   * 
   * Null instances must never be provided. An exception will be thrown if they are.
   * 
   */

  void accept(Object instance, Consumer<GQLValue> value);

  /**
   * Close this pipeline, indicating it will no longer be receiving any instances. The outstanding contexts should still be completed.
   */

  void complete();

}
