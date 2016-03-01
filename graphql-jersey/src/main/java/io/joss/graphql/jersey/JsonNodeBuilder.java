package io.joss.graphql.jersey;

import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.joss.graphql.core.value.GQLBooleanValue;
import io.joss.graphql.core.value.GQLEnumValueRef;
import io.joss.graphql.core.value.GQLFloatValue;
import io.joss.graphql.core.value.GQLIntValue;
import io.joss.graphql.core.value.GQLListValue;
import io.joss.graphql.core.value.GQLObjectValue;
import io.joss.graphql.core.value.GQLStringValue;
import io.joss.graphql.core.value.GQLValue;
import io.joss.graphql.core.value.GQLValueVisitor;
import io.joss.graphql.core.value.GQLVariableRef;

public class JsonNodeBuilder implements GQLValueVisitor<JsonNode>
{

  private static final JsonNodeBuilder generator = new JsonNodeBuilder();
  private final JsonNodeFactory factory;

  public JsonNodeBuilder(JsonNodeFactory factory)
  {
    this.factory = factory;
  }

  private JsonNodeBuilder()
  {
    this(JsonNodeFactory.instance);
  }

  @Override
  public JsonNode visitVarValue(GQLVariableRef value)
  {
    throw new RuntimeException("Can't include a variable reference output");
  }

  /**
   * 
   */

  @Override
  public ObjectNode visitObjectValue(GQLObjectValue value)
  {

    ObjectNode objectNode = factory.objectNode();

    for (Map.Entry<String, GQLValue> e : value.entries().entrySet())
    {
      if (e.getValue() == null)
      {
        objectNode.set(e.getKey(), factory.nullNode());
      }
      else
      {
        objectNode.set(e.getKey(), e.getValue().apply(this));
      }
    }
    return objectNode;
  }

  @Override
  public ArrayNode visitListValue(GQLListValue value)
  {
    ArrayNode arrayNode = factory.arrayNode();
    value.values().forEach(val -> arrayNode.add(val.apply(this)));
    return arrayNode;
  }

  @Override
  public JsonNode visitBooleanValue(GQLBooleanValue value)
  {
    return factory.booleanNode(value == GQLBooleanValue.TRUE);
  }

  @Override
  public JsonNode visitIntValue(GQLIntValue value)
  {
    return factory.numberNode(value.value());
  }

  @Override
  public JsonNode visitStringValue(GQLStringValue value)
  {
    return factory.textNode(value.value());
  }

  @Override
  public JsonNode visitFloatValue(GQLFloatValue value)
  {
    return factory.numberNode(value.value());
  }

  @Override
  public JsonNode visitEnumValueRef(GQLEnumValueRef value)
  {
    return factory.textNode(value.value());
  }

  public static JsonNodeBuilder defaultInstance()
  {
    return generator;
  }

  public static JsonNode toJsonNode(GQLValue value)
  {
    if (value == null)
    {
      return generator.factory.nullNode();
    }
    return value.apply(generator);
  }

  public static ArrayNode toJsonNode(GQLListValue value)
  {
    if (value == null)
    {
      return null;
    }
    return generator.visitListValue(value);
  }

  public static ObjectNode toJsonNode(GQLObjectValue value)
  {
    if (value == null)
    {
      return null;
    }
    return generator.visitObjectValue(value);
  }

}
