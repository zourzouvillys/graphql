package io.zrz.graphql.generator.java.codedom;

public interface StatementVisitor<R>
{

  R visitReturnStatement(ReturnStatement stmt);

  R visitExpressionStatement(ExpressionStatement stmt);

}
