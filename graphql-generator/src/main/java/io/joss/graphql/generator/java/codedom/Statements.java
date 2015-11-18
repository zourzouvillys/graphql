package io.joss.graphql.generator.java.codedom;

public class Statements
{

  public static Statement returnValue(Expression expr)
  {
    return ReturnStatement.builder().expression(expr).build();
  }

  public static Statement expression(Expression expr)
  {
    return ExpressionStatement.builder().expression(expr).build();
  }

  public static Statement assign(Expression left, Expression right)
  {
    if (left == null) 
      throw new IllegalArgumentException();
    if (right == null) 
      throw new IllegalArgumentException();
    return expression(AssignmentExpression.builder()
        .left(left)
        .operator("=")
        .right(right)
        .build());
  }

}
