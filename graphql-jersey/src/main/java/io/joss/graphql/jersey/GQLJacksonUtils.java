package io.joss.graphql.jersey;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import io.joss.graphql.core.value.GQLObjectValue;
import io.joss.graphql.core.value.GQLValue;
import io.joss.graphql.core.value.GQLValues;
import lombok.experimental.UtilityClass;

@UtilityClass
public class GQLJacksonUtils
{

  public static final GQLObjectValue convertToGQL(ObjectNode value)
  {
    Iterator<Map.Entry<String, JsonNode>> it = ((ObjectNode) value).fields();
    Map<String, GQLValue> map = Maps.newLinkedHashMap();
    while (it.hasNext())
    {
      Entry<String, JsonNode> e = it.next();
      map.put(e.getKey(), convertToGQL(e.getValue()));
    }
    return GQLValues.objectValue(map);
  }

  public static final GQLValue convertToGQL(JsonNode value)
  {

    if (value == null)
    {
      return null;
    }

    switch (value.getNodeType())
    {
      case ARRAY:
        List<GQLValue> values = Lists.newArrayList();
        for (JsonNode child : value)
        {
          values.add(convertToGQL(child));
        }
        return GQLValues.listValue(values);
      case BINARY:
        throw new RuntimeException("Not supported");
      case BOOLEAN:
        return GQLValues.booleanValue(value.asBoolean());
      case NUMBER:
        return GQLValues.floatValue(value.asDouble());
      case OBJECT:
        return convertToGQL((ObjectNode)value);
      case STRING:
        return GQLValues.stringValue(value.asText());
      case NULL:
      case MISSING:
      case POJO:
      default:
        break;
    }

    return GQLObjectValue.builder().value("in", GQLValues.booleanFalse()).build();
  }

}
