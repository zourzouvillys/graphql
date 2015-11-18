package io.joss.graphql.generator.java.codedom;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(builderClassName = "Builder")
public class FieldExpression implements Expression
{

  private final Expression expression;
  private final String name;

  @Override
  public <R> R apply(ExpressionVisitor<R> visitor)
  {
    return visitor.visitFieldExpression(this);
  }

}
