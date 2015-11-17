package io.joss.graphql.core.binder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Map;

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

public class JsonValueWriter implements GQLValueVisitor<Void>
{

  private BasicJsonGenerator g;

  public JsonValueWriter(BasicJsonGenerator g)
  {
    this.g = g;
  }

  @Override
  public Void visitVarValue(GQLVariableRef value)
  {
    throw new RuntimeException("Can't include a variable reference output");
  }

  @Override
  public Void visitObjectValue(GQLObjectValue value)
  {
    g.writeStartObject();
    for (Map.Entry<String, GQLValue> e : value.entries().entrySet())
    {
      g.writeFieldName(e.getKey());
      if (e.getValue() == null)
      {
        g.writeNull();
      }
      else
      {
        e.getValue().apply(this);
      }
    }
    g.writeEndObject();
    return null;
  }

  @Override
  public Void visitListValue(GQLListValue value)
  {
    g.writeStartArray();
    value.values().forEach(val -> val.apply(this));
    g.writeEndArray();
    return null;
  }

  @Override
  public Void visitBooleanValue(GQLBooleanValue value)
  {
    g.writeBoolean(value == GQLBooleanValue.TRUE);
    return null;
  }

  @Override
  public Void visitIntValue(GQLIntValue value)
  {
    g.writeNumber(value.value());
    return null;
  }

  @Override
  public Void visitStringValue(GQLStringValue value)
  {
    g.writeString(value.value());
    return null;
  }

  @Override
  public Void visitFloatValue(GQLFloatValue value)
  {
    g.writeNumber(value.value());
    return null;
  }

  @Override
  public Void visitEnumValueRef(GQLEnumValueRef value)
  {
    g.writeString(value.value());
    return null;
  }

  public static String toJSONString(GQLValue value)
  {
    try
    {
      ByteArrayOutputStream strm = new ByteArrayOutputStream();
      OutputStreamWriter writer = new OutputStreamWriter(strm);
      DirectJsonGenerator generator = new DirectJsonGenerator(writer);
      value.apply(new JsonValueWriter(generator));
      generator.flush();
      writer.flush();
      writer.close();
      return strm.toString();
    }
    catch (IOException ex)
    {
      throw new RuntimeException(ex);
    }
  }
}
