package io.zrz.graphql.core.utils;

import java.io.PrintStream;

import io.zrz.graphql.core.decl.GQLEnumDeclaration;
import io.zrz.graphql.core.decl.GQLInputTypeDeclaration;
import io.zrz.graphql.core.decl.GQLInterfaceTypeDeclaration;
import io.zrz.graphql.core.decl.GQLObjectTypeDeclaration;
import io.zrz.graphql.core.decl.GQLScalarTypeDeclaration;
import io.zrz.graphql.core.decl.GQLTypeDeclarationVisitor;
import io.zrz.graphql.core.decl.GQLUnionTypeDeclaration;
import io.zrz.graphql.core.lang.GQLTypeVisitor;
import io.zrz.graphql.core.types.GQLDeclarationRef;
import io.zrz.graphql.core.types.GQLListType;
import io.zrz.graphql.core.types.GQLNonNullType;

public class TypeRefPrinter implements GQLTypeVisitor<Void>, GQLTypeDeclarationVisitor<Void> {

  private final PrintStream out;

  public TypeRefPrinter(PrintStream out) {
    this.out = out;
  }

  @Override
  public Void visitUnion(GQLUnionTypeDeclaration type) {
    this.out.print(type.name());
    return null;
  }

  @Override
  public Void visitScalar(GQLScalarTypeDeclaration type) {
    this.out.print(type.name());
    return null;
  }

  @Override
  public Void visitObject(GQLObjectTypeDeclaration type) {
    this.out.print(type.name());
    return null;
  }

  @Override
  public Void visitNonNull(GQLNonNullType type) {
    type.type().apply(this);
    this.out.print("!");
    return null;
  }

  @Override
  public Void visitList(GQLListType type) {
    this.out.print("[");
    type.type().apply(this);
    this.out.print("]");
    return null;
  }

  @Override
  public Void visitInterface(GQLInterfaceTypeDeclaration type) {
    this.out.print(type.name());
    return null;
  }

  @Override
  public Void visitEnum(GQLEnumDeclaration type) {
    this.out.print(type.name());
    return null;
  }

  @Override
  public Void visitDeclarationRef(GQLDeclarationRef type) {
    this.out.print(type.name());
    return null;
  }

  @Override
  public Void visitInput(GQLInputTypeDeclaration type) {
    this.out.print(type.name());
    return null;
  }

}
