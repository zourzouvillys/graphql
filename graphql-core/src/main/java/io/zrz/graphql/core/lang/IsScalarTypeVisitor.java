package io.zrz.graphql.core.lang;

import io.zrz.graphql.core.decl.GQLEnumDeclaration;
import io.zrz.graphql.core.decl.GQLInputTypeDeclaration;
import io.zrz.graphql.core.decl.GQLInterfaceTypeDeclaration;
import io.zrz.graphql.core.decl.GQLObjectTypeDeclaration;
import io.zrz.graphql.core.decl.GQLScalarTypeDeclaration;
import io.zrz.graphql.core.decl.GQLTypeDeclarationVisitor;
import io.zrz.graphql.core.decl.GQLUnionTypeDeclaration;
import io.zrz.graphql.core.types.GQLDeclarationRef;
import io.zrz.graphql.core.types.GQLListType;
import io.zrz.graphql.core.types.GQLNonNullType;

public class IsScalarTypeVisitor implements GQLTypeDeclarationVisitor<Boolean>, GQLTypeVisitor<Boolean> {

  @Override
  public Boolean visitUnion(GQLUnionTypeDeclaration type) {
    return false;
  }

  @Override
  public Boolean visitScalar(GQLScalarTypeDeclaration type) {
    return true;
  }

  @Override
  public Boolean visitObject(GQLObjectTypeDeclaration type) {
    return false;
  }

  @Override
  public Boolean visitInterface(GQLInterfaceTypeDeclaration type) {
    return false;
  }

  @Override
  public Boolean visitEnum(GQLEnumDeclaration type) {
    return false;
  }

  ///

  @Override
  public Boolean visitNonNull(GQLNonNullType type) {
    return type.type().apply(this);
  }

  @Override
  public Boolean visitList(GQLListType type) {
    return type.type().apply(this);
  }

  @Override
  public Boolean visitDeclarationRef(GQLDeclarationRef type) {
    if (type.ref() == null) {
      throw new IllegalStateException(String.format("Internal Error: Attmpted to turn '%s' into a scalar, which had not been resolved.", type.name()));
    }
    return type.ref().apply(this);
  }

  @Override
  public Boolean visitInput(GQLInputTypeDeclaration type) {
    return false;
  }

}
