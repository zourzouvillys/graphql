package io.joss.graphql.core.parser;

public class SyntaxErrorException extends GQLException
{

  private static final long serialVersionUID = 1L;

  private ParseContext ctx;
  private String expected;
  private String message;

  public SyntaxErrorException(ParseContext ctx, String expected, String message)
  {
    super(String.format("syntax error at or near '%s': expected '%s' %s", (ctx.lexer().peek() == null) ? "EOF" : ctx.lexer().peek().toString(), expected, message));
    this.ctx = ctx;
    this.expected = expected;
    this.message = message;
  }

  public SyntaxErrorException(ParseContext ctx, String message)
  {
    super(String.format("syntax error at or near '%s': %s", (ctx.lexer().peek() == null) ? "EOF" : ctx.lexer().peek().toString(), message));
    this.ctx = ctx;
    this.message = message;
  }

  /**
   * returns the line (or a bunch of context) related to this error.
   * 
   * @return
   */

  public String line()
  {
    return ctx.lexer().input();
  }

  public int position()
  {
    if (ctx.lexer().peek() == null)
      return ctx.lexer().input().length();
    return this.ctx.lexer().peek().position().start();
  }

  public int lineNumber()
  {
    return 1;
  }

  public String expected()
  {
    return expected;
  }

  public String message()
  {
    return message;
  }

}
