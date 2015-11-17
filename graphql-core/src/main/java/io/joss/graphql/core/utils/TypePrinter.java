package io.joss.graphql.core.utils;

import java.io.PrintStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.joss.graphql.core.decl.GQLArgumentDefinition;
import io.joss.graphql.core.decl.GQLDeclarationVisitor;
import io.joss.graphql.core.decl.GQLEnumDeclaration;
import io.joss.graphql.core.decl.GQLInputFieldDeclaration;
import io.joss.graphql.core.decl.GQLInputTypeDeclaration;
import io.joss.graphql.core.decl.GQLInterfaceTypeDeclaration;
import io.joss.graphql.core.decl.GQLObjectTypeDeclaration;
import io.joss.graphql.core.decl.GQLParameterableFieldDeclaration;
import io.joss.graphql.core.decl.GQLScalarTypeDeclaration;
import io.joss.graphql.core.decl.GQLUnionTypeDeclaration;
import io.joss.graphql.core.lang.GQLTypeVisitor;
import io.joss.graphql.core.types.GQLDeclarationRef;
import io.joss.graphql.core.types.GQLListType;
import io.joss.graphql.core.types.GQLNonNullType;
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

public class TypePrinter implements GQLTypeVisitor<Void>, GQLValueVisitor<Void>, GQLDeclarationVisitor<Void>
{

  private PrintStream out;

  public TypePrinter(PrintStream out)
  {
    this.out = out;
  }

  private void printComment(String description, String pfx)
  {
    if (description != null && !description.isEmpty())
    {
      out.println();
      out.print(pfx);
      out.println("/**");
      out.print(pfx);
      out.print(" * ");
      out.println(description.replace("\n$",""));
      out.print(pfx);
      out.print(" */");
      out.println();
      out.println();
    }
  }

  @Override
  public Void visitUnion(GQLUnionTypeDeclaration type)
  {
    printComment(type.description(), "");
    out.print("union ");
    out.print(type.name());
    out.print(" = ");
    out.println(type.types().stream().map(t -> t.name()).collect(Collectors.joining(" | ")));
    return null;
  }

  @Override
  public Void visitScalar(GQLScalarTypeDeclaration type)
  {
    if (!type.isPrimitive())
    {
      printComment(type.description(), "");
      out.print("scalar ");
      out.println(type.name());
    }
    return null;
  }

  @Override
  public Void visitObject(GQLObjectTypeDeclaration type)
  {

    printComment(type.description(), "");

    out.print("type ");
    out.print(type.name());

    if (type.ifaces().size() > 0)
    {
      out.print(" implements ");
      out.println(type.ifaces().stream().map(t -> t.name()).collect(Collectors.joining(", ")));
    }

    out.println(" {");
    printFields(type.fields());
    out.println("}\n");
    return null;
  }

  private void printFields(List<GQLParameterableFieldDeclaration> fields)
  {
    fields.forEach(field -> {
      printComment(field.description(), "  ");
      out.print("  ");
      out.print(field.name());
      printArgumentDefinitions(field.args());
      out.print(": ");
      field.type().apply(new TypeRefPrinter(this.out));
      out.println();
    });
  }

  private void printInputFields(List<GQLInputFieldDeclaration> fields)
  {
    fields.forEach(field -> {
      printComment(field.description(), "  ");
      out.print("  ");
      out.print(field.name());
      out.print(": ");
      field.type().apply(new TypeRefPrinter(this.out));
      out.println();
    });
  }

  private void printArgumentDefinitions(List<GQLArgumentDefinition> args)
  {
    if (!args.isEmpty())
    {
      out.print("(");
      int c = 0;
      for (GQLArgumentDefinition arg : args)
      {
        if (c++ > 0)
        {
          out.print(", ");
        }
        out.print(arg.name());
        out.print(": ");
        arg.type().apply(new TypeRefPrinter(this.out));
        if (arg.defaultValue() != null)
        {
          out.print(" = ");
          arg.defaultValue().apply(this);
        }
      }
      out.print(")");
    }

  }

  @Override
  public Void visitNonNull(GQLNonNullType type)
  {
    type.type().apply(this);
    out.print("!");
    return null;
  }

  @Override
  public Void visitList(GQLListType type)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Void visitInterface(GQLInterfaceTypeDeclaration type)
  {
    out.print("interface ");
    out.print(type.name());

    if (type.ifaces().size() > 0)
    {
      out.print(" implements ");
      out.println(type.ifaces().stream().map(t -> t.name()).collect(Collectors.joining(", ")));
    }

    out.println(" {");
    printFields(type.fields());
    out.println("}");
    return null;

  }

  @Override
  public Void visitEnum(GQLEnumDeclaration type)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Void visitDeclarationRef(GQLDeclarationRef type)
  {
    // TODO Auto-generated method stub
    return null;
  }
  
  

  @Override
  public Void visitInput(GQLInputTypeDeclaration type)
  {

    printComment(type.description(), "");

    out.print("input ");
    out.print(type.name());
    out.println(" {");
    printInputFields(type.fields());
    out.println("}\n");
    return null;
  }

  ////
  //// --------------------------------- Values ---------------------------------
  ////

  @Override
  public Void visitVarValue(GQLVariableRef value)
  {
    out.print("$");
    out.print(value.name());
    return null;
  }

  @Override
  public Void visitListValue(GQLListValue value)
  {
    out.print("[ ");
    int c = 0;
    for (GQLValue val : value.values())
    {
      if (c++ > 0)
      {
        out.print(", ");
      }
      val.apply(this);
    }
    out.print(" ]");
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
        out.print("trie");
        break;
      default:
        throw new RuntimeException("tri-boolean?");
    }
    // TODO Auto-generated method stub
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
    out.print(value.value().replaceAll("\"", "\\\""));
    out.print('"');
    return null;
  }

  @Override
  public Void visitFloatValue(GQLFloatValue value)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Void visitEnumValueRef(GQLEnumValueRef value)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Void visitObjectValue(GQLObjectValue value)
  {
    out.print("{ ");
    int c = 0;
    for (Map.Entry<String, GQLValue> val : value.entries().entrySet())
    {
      if (c++ > 0)
      {
        out.print(", ");
      }
      out.print(val.getKey());
      out.print(": ");
      val.getValue().apply(this);
    }
    out.print(" }");
    return null;
  }

}
