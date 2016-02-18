package io.joss.graphql.executor;

import java.util.List;

import io.joss.graphql.core.doc.GQLArgument;
import io.joss.graphql.core.doc.GQLSelection;
import io.joss.graphql.executor.GraphQLOutputType.Field;

/**
 * performs a request on a field, returning a java object.
 */

@FunctionalInterface
public interface FieldHandler
{

  /**
   * Fetches the field value.
   * 
   * @param results
   *          Where to place the result.
   * 
   * @param roots
   *          The root objects. Note: ignore null ones.
   * 
   * @param type
   *          The type of the roots.
   * 
   * @param field
   *          The field we're fetching.
   * 
   * @param ctx
   *          The execution context.
   * 
   * @param args
   *          Any provided args.
   */

  Object[] value(Object[] roots, GraphQLOutputType type, Field field, ExecutionContext ctx, List<GQLArgument> args, List<GQLSelection> selection);

}
