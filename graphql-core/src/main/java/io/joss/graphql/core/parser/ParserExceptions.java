package io.joss.graphql.core.parser;

public class ParserExceptions
{

  /**
   * Throw an except that is somethign excepted.
   * 
   * @param message
   * 
   * @param parseContext
   * @param string
   * @return
   */

  public static SyntaxErrorException expect(ParseContext ctx, String expected, String message)
  {
    return new SyntaxErrorException(ctx, expected, message);
  }

  public static GQLException endOfStream()
  {
    return new GQLException("EOF");
  }

}
