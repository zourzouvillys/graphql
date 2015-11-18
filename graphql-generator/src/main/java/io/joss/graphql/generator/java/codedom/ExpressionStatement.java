package io.joss.graphql.generator.java.codedom;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(builderClassName = "Builder")
public class ExpressionStatement implements Statement
{
  
  private Expression expression;

  @Override
  public <R> R apply(StatementVisitor<R> visitor)
  {
    return visitor.visitExpressionStatement(this);
  }

}
