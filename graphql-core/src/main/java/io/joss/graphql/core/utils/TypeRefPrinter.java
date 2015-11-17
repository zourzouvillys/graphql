package io.joss.graphql.core.utils;

import java.io.PrintStream;

import io.joss.graphql.core.decl.GQLDeclarationVisitor;
import io.joss.graphql.core.decl.GQLEnumDeclaration;
import io.joss.graphql.core.decl.GQLInputTypeDeclaration;
import io.joss.graphql.core.decl.GQLInterfaceTypeDeclaration;
import io.joss.graphql.core.decl.GQLObjectTypeDeclaration;
import io.joss.graphql.core.decl.GQLScalarTypeDeclaration;
import io.joss.graphql.core.decl.GQLUnionTypeDeclaration;
import io.joss.graphql.core.lang.GQLTypeVisitor;
import io.joss.graphql.core.types.GQLDeclarationRef;
import io.joss.graphql.core.types.GQLListType;
import io.joss.graphql.core.types.GQLNonNullType;

public class TypeRefPrinter implements GQLTypeVisitor<Void>, GQLDeclarationVisitor<Void>
{

  private PrintStream out;

  public TypeRefPrinter(PrintStream out)
  {
    this.out = out;
  }

  @Override
  public Void visitUnion(GQLUnionTypeDeclaration type)
  {
    out.print(type.name());
    return null;
  }

  @Override
  public Void visitScalar(GQLScalarTypeDeclaration type)
  {
    out.print(type.name());
    return null;
  }

  @Override
  public Void visitObject(GQLObjectTypeDeclaration type)
  {
    out.print(type.name());
    return null;
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
    out.print("[");
    type.type().apply(this);
    out.print("]");
    return null;
  }

  @Override
  public Void visitInterface(GQLInterfaceTypeDeclaration type)
  {
    out.print(type.name());
    return null;
  }

  @Override
  public Void visitEnum(GQLEnumDeclaration type)
  {
    out.print(type.name());
    return null;
  }

  @Override
  public Void visitDeclarationRef(GQLDeclarationRef type)
  {
    out.print(type.name());
    return null;
  }

  @Override
  public Void visitInput(GQLInputTypeDeclaration type)
  {
    out.print(type.name());
    return null;
  }
}
