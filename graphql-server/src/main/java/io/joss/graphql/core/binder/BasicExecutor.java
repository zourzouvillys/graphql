package io.joss.graphql.core.binder;

import java.util.concurrent.atomic.AtomicReference;

import io.joss.graphql.core.binder.execution.QueryEnvironment;
import io.joss.graphql.core.binder.execution.planner.PreparedQuery;
import io.joss.graphql.core.binder.execution.planner.basic.BasicExecutionPlanner;
import io.joss.graphql.core.binder.runtime.DataContext;
import io.joss.graphql.core.binder.runtime.DataContexts;
import io.joss.graphql.core.doc.GQLDocument;
import io.joss.graphql.core.doc.GQLSelectedOperation;
import io.joss.graphql.core.parser.GQLParser;
import io.joss.graphql.core.value.GQLValue;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * Helper which does basic execution.
 * 
 * @author theo
 *
 */

@Slf4j
@UtilityClass
public class BasicExecutor
{

  //

  public static GQLValue simpleQuery(Object rootInstance, String query)
  {
    return simpleQuery(rootInstance, query, QueryEnvironment.emptyEnvironment());
  }

  public static GQLValue simpleQuery(Object rootInstance, String query, QueryEnvironment env)
  {

    // startup time:
    TypeBindingResult scanner = TypeScanner.bind(rootInstance.getClass());

    // parse incoming request:
    GQLDocument doc = GQLParser.parseDocument(query);

    DataContext root = DataContexts.build(scanner.registry(), scanner.root(), GQLSelectedOperation.defaultQuery(doc));

    // plan it:
    PreparedQuery prepared = new BasicExecutionPlanner(scanner.scanner()).plan(root);

    // execution time:
    log.info(" ---- Executing query");

    
    
    AtomicReference<GQLValue> result = new AtomicReference<GQLValue>();

    prepared.execute(rootInstance, env, completion -> {
      result.set(completion);
    });

    return result.get();

  }

}
