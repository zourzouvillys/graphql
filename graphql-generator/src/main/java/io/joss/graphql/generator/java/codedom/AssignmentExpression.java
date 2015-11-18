package io.joss.graphql.generator.java.codedom;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(builderClassName = "Builder")
public class AssignmentExpression implements Expression
{

  private Expression left;
  private String operator;
  private Expression right;

  @Override
  public <R> R apply(ExpressionVisitor<R> visitor)
  {
    return visitor.visitAssignmentExpression(this);
  }

}
