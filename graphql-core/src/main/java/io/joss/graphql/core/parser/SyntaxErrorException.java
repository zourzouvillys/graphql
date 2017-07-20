package io.joss.graphql.core.parser;

import io.joss.graphql.core.parser.Lexer.LineInfo;

public class SyntaxErrorException extends GQLException {

  private static final long serialVersionUID = 1L;

  private final ParseContext ctx;
  private String expected;
  private final String message;

  public SyntaxErrorException(ParseContext ctx, String expected, String message) {
    super(String.format("syntax error on %s at or near '%s': expected '%s' %s", calculateLine(ctx),
        (ctx.lexer().peek() == null) ? "EOF" : ctx.lexer().peek().toString(), expected, message));
    this.ctx = ctx;
    this.expected = expected;
    this.message = message;
  }

  public SyntaxErrorException(ParseContext ctx, String message) {
    super(String.format("syntax error on %s at or near '%s': %s", calculateLine(ctx), (ctx.lexer().peek() == null) ? "EOF" : ctx.lexer().peek().toString(),
        message));
    this.ctx = ctx;
    this.message = message;
  }

  private static LineInfo calculateLine(ParseContext ctx) {
    if (ctx.lexer().peek() == null) {
      return null;
    }
    final SourcePosition pos = ctx.lexer().peek().position();
    return ctx.lexer().lineNumberAtOffset(pos.start());
  }

  /**
   * returns the line (or a bunch of context) related to this error.
   *
   * @return
   */

  public String line() {
    return this.ctx.lexer().input();
  }

  public int position() {
    if (this.ctx.lexer().peek() == null) {
      return this.ctx.lexer().input().length();
    }
    return this.ctx.lexer().peek().position().start();
  }

  public int lineNumber() {
    return 1;
  }

  public String expected() {
    return this.expected;
  }

  public String message() {
    return this.message;
  }

}
