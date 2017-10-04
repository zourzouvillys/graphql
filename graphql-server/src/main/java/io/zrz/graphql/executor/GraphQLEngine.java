package io.zrz.graphql.executor;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;

import io.zrz.graphql.core.binder.execution.QueryEnvironment;
import io.zrz.graphql.core.doc.GQLOpType;
import io.zrz.graphql.core.doc.GQLSelectedOperation;
import io.zrz.graphql.core.parser.GQLException;
import io.zrz.graphql.core.types.GQLTypeReference;
import io.zrz.graphql.core.utils.GQLDocumentPrinter;
import io.zrz.graphql.core.value.GQLObjectValue;
import io.zrz.graphql.schema.__Schema;
import io.zrz.graphql.schema.__Type;
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

  public Collection<GraphQLInputType> inputTypes()
  {
    return app.inputTypes();
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

    log.debug("Executing GQL query: {}", new GQLDocumentPrinter().serialize(op.doc()));

    try
    {
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

      GQLObjectValue res = builder.selections(type, ret, op.operation().selections())[0];

      log.trace("GQL result: {}", res);

      return res;

    }
    catch (Exception ex)
    {
      log.warn("Exception caught processing GQL query", ex.getMessage(), ex);
      throw ex;
    }
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
    List<__Type> types = app.types().stream().filter(t -> !t.name().startsWith("__")).map(__Type::new).collect(Collectors.toList());
    types.addAll(app.inputTypes().stream().filter(t -> !t.name().startsWith("__")).map(__Type::new).collect(Collectors.toList()));
    return new __Schema(new __Type(app.queryRoot()), app.mutationRoot() != null ? new __Type(app.mutationRoot()) : null, types);
  }

  public __Type schema(String name)
  {
    GraphQLOutputType input = app.type(name);
    if (input != null)
    {
      return new __Type(input);
    }
    GraphQLInputType output = app.inputType(name);
    if (output != null)
    {
      return new __Type(output);
    }
    return null;
  }

  /**
   * returns the specified __Type for the given type reference.
   * 
   * @param type
   * @return
   */

  public __Type type(GQLTypeReference type)
  {
    return __Type.type(this, type);
  }

  public GraphQLInputType inputType(String name)
  {
    return this.app.inputType(name);
  }

}
