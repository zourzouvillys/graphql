package io.zrz.graphql.core.utils;

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

public class TypeRefStringGenerator implements GQLTypeVisitor<String>, GQLTypeDeclarationVisitor<String> {

  private static final TypeRefStringGenerator INSTANCE = new TypeRefStringGenerator();

  protected TypeRefStringGenerator() {
  }

  public static TypeRefStringGenerator getInstance() {
    return INSTANCE;
  }

  @Override
  public String visitUnion(GQLUnionTypeDeclaration type) {
    return type.name();
  }

  @Override
  public String visitScalar(GQLScalarTypeDeclaration type) {
    return type.name();
  }

  @Override
  public String visitObject(GQLObjectTypeDeclaration type) {
    return type.name();
  }

  @Override
  public String visitNonNull(GQLNonNullType type) {
    return String.format("%s!", type.type().apply(this));
  }

  @Override
  public String visitList(GQLListType type) {
    return String.format("[%s]", type.type().apply(this));
  }

  @Override
  public String visitInterface(GQLInterfaceTypeDeclaration type) {
    return type.name();
  }

  @Override
  public String visitEnum(GQLEnumDeclaration type) {
    return type.name();
  }

  @Override
  public String visitDeclarationRef(GQLDeclarationRef type) {
    return type.name();
  }

  @Override
  public String visitInput(GQLInputTypeDeclaration type) {
    return type.name();
  }

}
