package io.joss.graphql.executor;

import org.junit.Test;

import io.joss.graphql.core.binder.execution.QueryEnvironment;
import io.joss.graphql.core.doc.GQLSelectedOperation;
import io.joss.graphql.core.value.GQLObjectValue;
import io.joss.graphql.executor.fixtures.Test1Root;
import io.joss.graphql.executor.fixtures.Test2Root;

public class GraphQLEngineTest
{

  @Test
  public void test()
  {
    
    GraphQLEngineConfig app = new GraphQLEngineConfig();
    
    app.queryRoot(app.registerType(GraphQLOutputType.builder(Test1Root.class).build()));
    app.registerType(GraphQLOutputType.builder(Test2Root.class).build());
    
    GraphQLEngine engine = new GraphQLEngine(app);
    
    GQLObjectValue val = engine.execute(
        QueryEnvironment.emptyEnvironment(), 
        GQLSelectedOperation.defaultQuery("{ id, simplelist, value, child { id1: id }, children { subid: id } }"),
        new Test1Root(),
        null);
    
    System.err.println(val);
    
    // fail("Not yet implemented");
    
  }

}
