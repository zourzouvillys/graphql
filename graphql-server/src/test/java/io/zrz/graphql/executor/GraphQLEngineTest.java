package io.zrz.graphql.executor;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import io.zrz.graphql.core.binder.execution.QueryEnvironment;
import io.zrz.graphql.core.doc.GQLSelectedOperation;
import io.zrz.graphql.core.value.GQLObjectValue;
import io.zrz.graphql.core.value.GQLValue;
import io.zrz.graphql.core.value.GQLValueConverters;
import io.zrz.graphql.core.value.GQLValues;
import io.zrz.graphql.executor.GraphQLEngine;
import io.zrz.graphql.executor.GraphQLEngineConfig;
import io.zrz.graphql.executor.GraphQLOutputType;
import io.zrz.graphql.executor.fixtures.Test1Root;
import io.zrz.graphql.executor.fixtures.Test2Root;
import io.zrz.graphql.executor.fixtures.Test3Root;

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
  
  @Test  
  public void testConstantArg()
  {
    
    GraphQLEngineConfig app = new GraphQLEngineConfig();
    app.queryRoot(app.registerType(GraphQLOutputType.builder(Test3Root.class).build()));    
    GraphQLEngine engine = new GraphQLEngine(app);
    
    GQLObjectValue val = engine.execute(
        QueryEnvironment.emptyEnvironment(), 
        GQLSelectedOperation.defaultQuery("{ ret1: stringArg(arg1: 'aaa') }"),
        new Test3Root(),
        null);
    
    assertEquals("aaa", val.entries().get("ret1").apply(GQLValueConverters.stringConverter()));
    
  }  
  
  @Test  
  public void testArgInputString()
  {
    
    GraphQLEngineConfig app = new GraphQLEngineConfig();
    app.queryRoot(app.registerType(GraphQLOutputType.builder(Test3Root.class).build()));    
    GraphQLEngine engine = new GraphQLEngine(app);
    
    Map<String, GQLValue> input = new HashMap<>();

    input.put("arg1", GQLValues.stringValue("hello, world"));
    
    GQLObjectValue val = engine.execute(
        QueryEnvironment.emptyEnvironment(), 
        GQLSelectedOperation.defaultQuery("{ ret1: stringArg(arg1: $arg1) }"),
        new Test3Root(),
        GQLValues.objectValue(input));
    
    assertEquals("hello, world", val.entries().get("ret1").apply(GQLValueConverters.stringConverter()));
    
    // fail("Not yet implemented");
    
  }
  
  
  @Test  
  public void testArgInputInt()
  {
    
    GraphQLEngineConfig app = new GraphQLEngineConfig();
    app.queryRoot(app.registerType(GraphQLOutputType.builder(Test3Root.class).build()));    
    GraphQLEngine engine = new GraphQLEngine(app);
    
    Map<String, GQLValue> input = new HashMap<>();

    input.put("arg1", GQLValues.intValue(123));
    
    GQLObjectValue val = engine.execute(
        QueryEnvironment.emptyEnvironment(), 
        GQLSelectedOperation.defaultQuery("{ ret1: intArg(arg1: $arg1) }"),
        new Test3Root(),
        GQLValues.objectValue(input));
    
    assertEquals("123", val.entries().get("ret1").apply(GQLValueConverters.stringConverter()));
    
    // fail("Not yet implemented");
    
  }

  @Test  
  public void testFragmentSpread()
  {
    
    GraphQLEngineConfig app = new GraphQLEngineConfig();
    app.queryRoot(app.registerType(GraphQLOutputType.builder(Test1Root.class).build()));
    app.registerType(GraphQLOutputType.builder(Test2Root.class).build());
    GraphQLEngine engine = new GraphQLEngine(app);
    
    Map<String, GQLValue> input = new HashMap<>();

    input.put("arg1", GQLValues.intValue(123));
    
    GQLObjectValue val = engine.execute(
        QueryEnvironment.emptyEnvironment(), 
        GQLSelectedOperation.defaultQuery("query App { child { x: id, ...F0 } } fragment F0 on Test2Root { id }"),
        new Test1Root(),
        GQLValues.objectValue(input));
    
    System.err.println(val);

    assertEquals("123", val.entries().get("ret1").apply(GQLValueConverters.stringConverter()));
    
    // fail("Not yet implemented");
    
  }

}
