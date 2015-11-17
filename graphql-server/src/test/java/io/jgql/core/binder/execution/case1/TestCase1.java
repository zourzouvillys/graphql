package io.jgql.core.binder.execution.case1;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import io.joss.graphql.core.binder.BasicExecutor;
import io.joss.graphql.core.binder.annotatons.GQLField;
import io.joss.graphql.core.binder.annotatons.GQLType;
import io.joss.graphql.core.value.GQLObjectValue;
import io.joss.graphql.core.value.GQLValue;

public class TestCase1
{

  @GQLType
  public static class MyChild
  {

    @GQLField
    public MyChild self()
    {
      return new MyChild();
    }

    @GQLField
    public String id()
    {
      return "cid";
    }

  }


  @GQLType
  public static class MyRoot
  {

    @GQLField
    public MyChild child()
    {
      return new MyChild();
    }

    @GQLField
    public String id()
    {
      return "pid";
    }

  }

  @Test
  public void test()
  {
    GQLValue value = BasicExecutor.simpleQuery(new MyRoot(), "{ child { self { id } } }");    
    assertEquals("returned type wasn't an object value", GQLObjectValue.class, ((GQLObjectValue) value).entries().get("child").getClass());
  }

}
