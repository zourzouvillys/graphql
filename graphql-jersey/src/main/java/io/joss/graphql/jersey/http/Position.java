package io.joss.graphql.jersey.http;

import io.joss.graphql.core.parser.SyntaxErrorException;
import lombok.Value;

@Value
public class Position
{
  
  private int line;
  private int position;

  public static Position create(SyntaxErrorException ex)
  {
    return new Position(ex.lineNumber(), ex.position());
  }

}
