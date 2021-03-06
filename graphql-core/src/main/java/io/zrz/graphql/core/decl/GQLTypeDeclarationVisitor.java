package io.zrz.graphql.core.decl;

public interface GQLTypeDeclarationVisitor<R> {

  R visitUnion(GQLUnionTypeDeclaration type);

  R visitScalar(GQLScalarTypeDeclaration type);

  R visitObject(GQLObjectTypeDeclaration type);

  R visitInterface(GQLInterfaceTypeDeclaration type);

  R visitEnum(GQLEnumDeclaration type);

  R visitInput(GQLInputTypeDeclaration type);

}
