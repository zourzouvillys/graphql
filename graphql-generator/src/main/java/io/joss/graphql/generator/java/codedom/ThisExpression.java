package io.joss.graphql.generator.java.codedom;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(builderClassName = "Builder")
public class ThisExpression implements Expression
{

   @Override
  public <R> R apply(ExpressionVisitor<R> visitor)
  {
    return visitor.visitThisExpression(this);
  }

  public static Expression instance()
  {
    return new ThisExpression();
  }

}
