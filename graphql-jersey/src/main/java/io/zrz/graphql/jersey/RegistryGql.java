package io.zrz.graphql.jersey;


import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.concurrent.atomic.AtomicReference;

import io.zrz.graphql.core.binder.TypeBindingResult;
import io.zrz.graphql.core.binder.TypeScanner;
import io.zrz.graphql.core.binder.execution.QueryEnvironment;
import io.zrz.graphql.core.binder.execution.planner.ExecutionPlanner;
import io.zrz.graphql.core.binder.execution.planner.basic.BasicExecutionPlanner;
import io.zrz.graphql.core.binder.runtime.DataContext;
import io.zrz.graphql.core.binder.runtime.DataContexts;
import io.zrz.graphql.core.decl.GQLObjectTypeDeclaration;
import io.zrz.graphql.core.doc.GQLOpType;
import io.zrz.graphql.core.doc.GQLSelectedOperation;
import io.zrz.graphql.core.lang.GQLSchemaBuilder;
import io.zrz.graphql.core.types.GQLTypes;
import io.zrz.graphql.core.utils.TypePrinter;
import io.zrz.graphql.core.value.GQLObjectValue;
import io.zrz.graphql.core.value.GQLValue;

/**
 * hides the various methods of GQL.
 * 
 * @author theo
 *
 */
public class RegistryGql
{

  private final TypeBindingResult scanner;
  private final BasicExecutionPlanner planner;
  private final String schema;

  public RegistryGql(Class<?> klass, MutationRegistry registry)
  {

    // initialise the builder.
    GQLSchemaBuilder builder = new GQLSchemaBuilder();
    builder.add(GQLTypes.builtins());

    // scanner for adding types from java objects.
    TypeScanner scanner = new TypeScanner(builder);

    // add our root query class.
    GQLObjectTypeDeclaration root = (GQLObjectTypeDeclaration) scanner.add(klass);

    GQLObjectTypeDeclaration mutationRoot = registry.build(scanner);

    builder.add(mutationRoot);
    
    // finalize any hinted but unregistered typed.
    scanner.finish();

    // create the scanner.
    this.scanner = new TypeBindingResult(builder.build(), scanner, root, mutationRoot);
    this.planner = new BasicExecutionPlanner(this.scanner.scanner());

    // plan.
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    this.scanner.registry().apply(new TypePrinter(new PrintStream(buffer)));
    this.schema = buffer.toString();

  }


  public String schema()
  {
    return this.schema;
  }

  public GQLObjectValue query(Object root, GQLSelectedOperation query, QueryEnvironment env, GQLValue input)
  {
    final DataContext ctx = analize(query);
    try
    {
      AtomicReference<GQLValue> res = new AtomicReference<GQLValue>();
      planner().plan(ctx).execute(root, env, value -> res.set(value));
      return (GQLObjectValue) res.get();
    }
    finally
    {
    }
  }

  /**
   * Performs analysis of a query.
   * 
   * @param query
   * @return
   */

  private DataContext analize(GQLSelectedOperation query)
  {
    if (query.operation().type() == GQLOpType.Mutation)
    {
      return DataContexts.build(this.scanner.registry(), scanner.mutation(), query);
    }
    return DataContexts.build(this.scanner.registry(), scanner.root(), query);
  }

  /**
   * returns the planner in use.
   */

  private ExecutionPlanner planner()
  {
    return this.planner;
  }

}