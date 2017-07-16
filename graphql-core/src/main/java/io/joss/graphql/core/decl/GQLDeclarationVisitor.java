package io.joss.graphql.core.decl;

public interface GQLDeclarationVisitor<R> {

  R visitTypeDeclaration(GQLTypeDeclaration typedecl);

  R visitSchemaDeclaration(GQLSchemaDeclaration gqlSchemaDeclaration);

}
