package io.joss.graphql.generator.java.codedom;

public interface BodyDeclarationVisitor<T>
{

  T visitMethod(MethodDeclaration method);

  T visitTypeDeclaration(TypeDeclaration type);

  T visitField(FieldDeclaration field);

}
