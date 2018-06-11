package io.zrz.graphql.core.utils;

import java.io.PrintStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.zrz.graphql.core.decl.GQLArgumentDefinition;
import io.zrz.graphql.core.decl.GQLEnumDeclaration;
import io.zrz.graphql.core.decl.GQLInputFieldDeclaration;
import io.zrz.graphql.core.decl.GQLInputTypeDeclaration;
import io.zrz.graphql.core.decl.GQLInterfaceTypeDeclaration;
import io.zrz.graphql.core.decl.GQLObjectTypeDeclaration;
import io.zrz.graphql.core.decl.GQLParameterableFieldDeclaration;
import io.zrz.graphql.core.decl.GQLScalarTypeDeclaration;
import io.zrz.graphql.core.decl.GQLTypeDeclarationVisitor;
import io.zrz.graphql.core.decl.GQLUnionTypeDeclaration;
import io.zrz.graphql.core.lang.GQLTypeVisitor;
import io.zrz.graphql.core.types.GQLDeclarationRef;
import io.zrz.graphql.core.types.GQLListType;
import io.zrz.graphql.core.types.GQLNonNullType;
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

public class TypePrinter implements GQLTypeVisitor<Void>, GQLValueVisitor<Void>, GQLTypeDeclarationVisitor<Void> {

  private final PrintStream out;

  public TypePrinter(PrintStream out) {
    this.out = out;
  }

  private void printComment(String description, String pfx) {
    if (description != null && !description.isEmpty()) {
      this.out.println();
      this.out.print(pfx);
      this.out.println("/**");
      this.out.print(pfx);
      this.out.print(" * ");
      this.out.println(description.replace("\n$", ""));
      this.out.print(pfx);
      this.out.print(" */");
      this.out.println();
      this.out.println();
    }
  }

  @Override
  public Void visitUnion(GQLUnionTypeDeclaration type) {
    this.printComment(type.description(), "");
    this.out.print("union ");
    this.out.print(type.name());
    this.out.print(" = ");
    this.out.println(type.types().stream().map(t -> t.name()).collect(Collectors.joining(" | ")));
    return null;
  }

  @Override
  public Void visitScalar(GQLScalarTypeDeclaration type) {
    if (!type.isPrimitive()) {
      this.printComment(type.description(), "");
      this.out.print("scalar ");
      this.out.println(type.name());
    }
    return null;
  }

  @Override
  public Void visitObject(GQLObjectTypeDeclaration type) {

    this.printComment(type.description(), "");

    this.out.print("type ");
    this.out.print(type.name());

    if (type.ifaces().size() > 0) {
      this.out.print(" implements ");
      this.out.println(type.ifaces().stream().map(t -> t.name()).collect(Collectors.joining(", ")));
    }

    this.out.println(" {");
    this.printFields(type.fields());
    this.out.println("}\n");
    return null;
  }

  private void printFields(List<GQLParameterableFieldDeclaration> fields) {
    fields.forEach(field -> {
      this.printComment(field.description(), "  ");
      this.out.print("  ");
      this.out.print(field.name());
      this.printArgumentDefinitions(field.args());
      this.out.print(": ");
      field.type().apply(new TypeRefPrinter(this.out));
      this.out.println();
    });
  }

  private void printInputFields(List<GQLInputFieldDeclaration> fields) {
    fields.forEach(field -> {
      this.printComment(field.description(), "  ");
      this.out.print("  ");
      this.out.print(field.name());
      this.out.print(": ");
      field.type().apply(new TypeRefPrinter(this.out));
      this.out.println();
    });
  }

  private void printArgumentDefinitions(List<GQLArgumentDefinition> args) {
    if (!args.isEmpty()) {
      this.out.print("(");
      int c = 0;
      for (final GQLArgumentDefinition arg : args) {
        if (c++ > 0) {
          this.out.print(", ");
        }
        this.out.print(arg.name());
        this.out.print(": ");
        arg.type().apply(new TypeRefPrinter(this.out));

        arg.defaultValue().ifPresent(val -> {

          this.out.print(" = ");
          val.apply(this);

        });

      }
      this.out.print(")");
    }

  }

  @Override
  public Void visitNonNull(GQLNonNullType type) {
    type.type().apply(this);
    this.out.print("!");
    return null;
  }

  @Override
  public Void visitList(GQLListType type) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Void visitInterface(GQLInterfaceTypeDeclaration type) {
    this.out.print("interface ");
    this.out.print(type.name());

    if (type.ifaces().size() > 0) {
      this.out.print(" implements ");
      this.out.println(type.ifaces().stream().map(t -> t.name()).collect(Collectors.joining(", ")));
    }

    this.out.println(" {");
    this.printFields(type.fields());
    this.out.println("}");
    return null;

  }

  @Override
  public Void visitEnum(GQLEnumDeclaration type) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Void visitDeclarationRef(GQLDeclarationRef type) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Void visitInput(GQLInputTypeDeclaration type) {

    this.printComment(type.description(), "");

    this.out.print("input ");
    this.out.print(type.name());
    this.out.println(" {");
    this.printInputFields(type.fields());
    this.out.println("}\n");
    return null;
  }

  ////
  //// --------------------------------- Values
  //// ---------------------------------
  ////

  @Override
  public Void visitVarValue(GQLVariableRef value) {
    this.out.print("$");
    this.out.print(value.name());
    return null;
  }

  @Override
  public Void visitListValue(GQLListValue value) {
    this.out.print("[ ");
    int c = 0;
    for (final GQLValue val : value.values()) {
      if (c++ > 0) {
        this.out.print(", ");
      }
      val.apply(this);
    }
    this.out.print(" ]");
    return null;
  }

  @Override
  public Void visitBooleanValue(GQLBooleanValue value) {
    switch (value) {
      case FALSE:
        this.out.print("false");
        break;
      case TRUE:
        this.out.print("trie");
        break;
      default:
        throw new RuntimeException("tri-boolean?");
    }
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Void visitIntValue(GQLIntValue value) {
    this.out.print(value.value());
    return null;
  }

  @Override
  public Void visitStringValue(GQLStringValue value) {
    this.out.print('"');
    this.out.print(value.value().replaceAll("\"", "\\\""));
    this.out.print('"');
    return null;
  }

  @Override
  public Void visitFloatValue(GQLFloatValue value) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Void visitEnumValueRef(GQLEnumValueRef value) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Void visitObjectValue(GQLObjectValue value) {
    this.out.print("{ ");
    int c = 0;
    for (final Map.Entry<String, GQLValue> val : value.entries().entrySet()) {
      if (c++ > 0) {
        this.out.print(", ");
      }
      this.out.print(val.getKey());
      this.out.print(": ");
      val.getValue().apply(this);
    }
    this.out.print(" }");
    return null;
  }

}
