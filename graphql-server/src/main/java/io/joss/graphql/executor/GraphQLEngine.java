package io.joss.graphql.executor;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;

import io.joss.graphql.core.binder.execution.QueryEnvironment;
import io.joss.graphql.core.doc.GQLOpType;
import io.joss.graphql.core.doc.GQLSelectedOperation;
import io.joss.graphql.core.parser.GQLException;
import io.joss.graphql.core.utils.DefinitionPrinter;
import io.joss.graphql.core.utils.GQLDocumentPrinter;
import io.joss.graphql.core.value.GQLObjectValue;
import io.joss.graphql.schema.__Schema;
import io.joss.graphql.schema.__Type;
import lombok.extern.slf4j.Slf4j;

/**
 * Entry point for executing GraphQL documents.
 * 
 * @author theo
 *
 */

@Slf4j
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

  public Collection<GraphQLOutputType> types()
  {
    return app.types();
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

    log.debug("Executing: {}", new GQLDocumentPrinter().serialize(op.doc()));

    //
    Map<Class<?>, Object> ctx = new HashMap<>(env.getContexts());
    ctx.put(GraphQLEngine.class, this);

    // generate an execution plan.
    ExecutionContext builder = new ExecutionContext(this, env.withContexts(ctx), op);

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

  public GraphQLOutputType type(String name)
  {
    return this.app.type(name);
  }

  public __Schema schema()
  {
    return new __Schema(new __Type(app.queryRoot()), app.types().stream().filter(t -> !t.name().startsWith("__")).map(__Type::new).collect(Collectors.toList()));
  }

  public __Type schema(String name)
  {
    return new __Type(app.type(name));
  }

}
