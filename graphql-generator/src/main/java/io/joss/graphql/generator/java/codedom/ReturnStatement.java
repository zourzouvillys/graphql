package io.joss.graphql.generator.java.codedom;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(builderClassName = "Builder")
public class ReturnStatement implements Statement
{
  
  private final Expression expression;

  @Override
  public <R> R apply(StatementVisitor<R> visitor)
  {
    return visitor.visitReturnStatement(this);
  }

}
