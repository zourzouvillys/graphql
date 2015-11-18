package io.joss.graphql.client.channel.loopback;

import java.util.concurrent.atomic.AtomicReference;

import io.jgql.core.binder.execution.case1.TestCase1.MyRoot;
import io.joss.graphql.client.runtime.GQLChannel;
import io.joss.graphql.client.runtime.GQLExecution;
import io.joss.graphql.client.runtime.GQLPreparedStatement;
import io.joss.graphql.core.binder.BasicExecutor;
import io.joss.graphql.core.binder.TypeBindingResult;
import io.joss.graphql.core.binder.TypeScanner;
import io.joss.graphql.core.binder.execution.QueryEnvironment;
import io.joss.graphql.core.binder.execution.planner.PreparedQuery;
import io.joss.graphql.core.binder.execution.planner.basic.BasicExecutionPlanner;
import io.joss.graphql.core.binder.runtime.DataContext;
import io.joss.graphql.core.binder.runtime.DataContexts;
import io.joss.graphql.core.doc.GQLDocument;
import io.joss.graphql.core.doc.GQLSelectedOperation;
import io.joss.graphql.core.parser.GQLParser;
import io.joss.graphql.core.value.GQLObjectValue;
import io.joss.graphql.core.value.GQLValue;

public class LoopbackChannel<Q> implements GQLChannel
{

  private Class<Q> queryType;
  private Q instance;
  private TypeBindingResult scanner;

  public LoopbackChannel(Class<Q> queryType, Q instance)
  {
    this.queryType = queryType;
    this.instance = instance;
    this.scanner = TypeScanner.bind(queryType);

  }

  @Override
  public GQLPreparedStatement prepare(GQLDocument doc)
  {

    return new GQLPreparedStatement() {

      @Override
      public GQLExecution execute(String named, GQLObjectValue input)
      {
        return LoopbackChannel.this.execute(doc, named, input);
      }

    };

  }

  /**
   * 
   */

  public GQLExecution execute(GQLDocument doc, String named, GQLObjectValue input)
  {
    
    return new GQLExecution() {
      
      @Override
      public GQLObjectValue get()
      {

        DataContext root = DataContexts.build(scanner.registry(), scanner.root(), GQLSelectedOperation.defaultQuery(doc));

        PreparedQuery prepared = new BasicExecutionPlanner(scanner.scanner()).plan(root);

        AtomicReference<GQLValue> result = new AtomicReference<GQLValue>();

        prepared.execute(instance, QueryEnvironment.emptyEnvironment(), completion -> {
          result.set(completion);
        });

        return (GQLObjectValue) result.get();

      }
      
    };
    
  }

}
