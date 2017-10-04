package io.zrz.graphql.core.utils;

import java.io.PrintStream;
import java.util.Map.Entry;

import io.zrz.graphql.core.value.GQLBooleanValue;
import io.zrz.graphql.core.value.GQLEnumValueRef;
import io.zrz.graphql.core.value.GQLFloatValue;
import io.zrz.graphql.core.value.GQLIntValue;
import io.zrz.graphql.core.value.GQLListValue;
import io.zrz.graphql.core.value.GQLObjectValue;
import io.zrz.graphql.core.value.GQLStringValue;
import io.zrz.graphql.core.value.GQLValue;
import io.zrz.graphql.core.value.GQLValueVisitor;
import io.zrz.graphql.core.value.GQLVariableRef;

public class GQLJsonValueWriter implements GQLValueVisitor<Void>
{

  private PrintStream out;
  int depth = 0;

  public GQLJsonValueWriter(PrintStream out)
  {
    this.out = out;
  }

  void start()
  {
    for (int i = 0; i < depth; ++i)
    {
      out.print("  ");
    }
  }

  @Override
  public Void visitVarValue(GQLVariableRef value)
  {
    throw new RuntimeException();
  }

  @Override
  public Void visitObjectValue(GQLObjectValue value)
  {

    out.println();
    start();
    depth++;

    out.println("{");
    start();

    int count = 0;

    for (Entry<String, GQLValue> entry : value.entries().entrySet())
    {
      
      if (count++ > 0)
      {
        out.println(',');
        start();
      }
      
      out.print('"');
      out.print(entry.getKey());
      out.print('"');
      out.print(": ");
      if (entry.getValue() == null)
      {
        out.print("null");
      }
      else
      {
        entry.getValue().apply(this);
      }
      
    }

    depth--;

    out.println();
    start();


    out.println("}");
    start();


    return null;
  }

  @Override
  public Void visitListValue(GQLListValue value)
  {
    out.print("[");
    depth++;
    int count = 0;
    for (GQLValue elt : value.values())
    {
      if (count++ > 0)
      {
        out.print(", ");
      }
      if (elt == null)
      {
        out.print("null");
      }
      else
      {
        elt.apply(this);
      }
    }
    depth--;
    out.print("]");
    return null;
  }

  @Override
  public Void visitBooleanValue(GQLBooleanValue value)
  {
    switch (value)
    {
      case FALSE:
        out.print("false");
        break;
      case TRUE:
        out.print("true");
        break;
      default:
        break;

    }
    return null;
  }

  @Override
  public Void visitIntValue(GQLIntValue value)
  {
    out.print(value.value());
    return null;
  }

  @Override
  public Void visitStringValue(GQLStringValue value)
  {
    out.print('"');
    out.print(value.value());
    out.print('"');
    return null;
  }

  @Override
  public Void visitFloatValue(GQLFloatValue value)
  {
    out.print(value.value());
    return null;
  }

  @Override
  public Void visitEnumValueRef(GQLEnumValueRef value)
  {
    out.print('"');
    out.print(value.value());
    out.print('"');
    return null;
  }

}
