package io.joss.graphql.executor;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import io.joss.graphql.core.types.GQLTypes;
import io.joss.graphql.executor.fixtures.Test1Root;
import io.joss.graphql.executor.fixtures.Test2Root;

public class GraphQLOutputTypeTest
{

  @Test
  public void testOverridenField()
  {
    GraphQLOutputType res = GraphQLOutputType.builder(Test2Root.class).build();
    assertEquals(5, res.fields().size());
  }

  @Test
  public void testArgGeneration()
  {
    GraphQLOutputType res = GraphQLOutputType.builder(Test1Root.class).build();
    assertEquals(1, res.field("value").args().size());
    assertEquals("in", res.field("value").args().get(0).name());
  }

  @Test(expected = IllegalStateException.class)
  public void testFailure()
  {
    GraphQLOutputType.Builder builder = GraphQLOutputType.builder("TestFailure");
    builder.addField("iD").type(GQLTypes.stringType());
    builder.addField("Id").type(GQLTypes.stringType());
    builder.build();
  }

}
