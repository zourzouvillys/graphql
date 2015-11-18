package io.joss.graphql.generator.java.codedom;

public interface ExpressionVisitor<R>
{

  R visitFieldExpression(FieldExpression expr);

  R visitThisExpression(ThisExpression expr);

  R visitAssignmentExpression(AssignmentExpression expr);

  R visitSimpleNameExpression(SimpleNameExpression expr);

}
