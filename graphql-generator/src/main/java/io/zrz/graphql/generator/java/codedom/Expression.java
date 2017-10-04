package io.zrz.graphql.generator.java.codedom;

public interface Expression
{

  <R> R apply(ExpressionVisitor<R> visitor);
  
}
