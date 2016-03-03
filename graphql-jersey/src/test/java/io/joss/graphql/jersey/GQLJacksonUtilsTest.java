package io.joss.graphql.jersey;

import static org.junit.Assert.*;

import org.junit.Test;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.joss.graphql.core.value.GQLObjectValue;

public class GQLJacksonUtilsTest
{

  @Test
  public void test()
  {
    ObjectNode node = JsonNodeFactory.instance.objectNode();
    node.put("name", JsonNodeFactory.instance.nullNode());
    GQLObjectValue res = GQLJacksonUtils.convertToGQL(node);
    assertNull(res.entries().get("name"));    
  }

}
