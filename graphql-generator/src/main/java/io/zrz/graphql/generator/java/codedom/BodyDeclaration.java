package io.zrz.graphql.generator.java.codedom;

public interface BodyDeclaration
{

  <R> R apply(BodyDeclarationVisitor<R> visitor);

}
