package io.joss.graphql.generator.java.codedom;

public interface Statement
{
  
  <R> R apply(StatementVisitor<R> visitor);

}
