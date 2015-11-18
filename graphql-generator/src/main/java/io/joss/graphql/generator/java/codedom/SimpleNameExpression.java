package io.joss.graphql.generator.java.codedom;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(builderClassName = "Builder")
public class SimpleNameExpression implements NameExpression
{
  
  private String name;

  @Override
  public <R> R apply(ExpressionVisitor<R> visitor)
  {
    return visitor.visitSimpleNameExpression(this);
  }

}
