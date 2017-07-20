package io.joss.graphql.core.parser;

import io.joss.graphql.core.parser.Lexer.TokenType;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class Token {

  private final TokenType type;
  private final String value;
  private final SourcePosition position;

  public Token(final TokenType type, final String value, SourcePosition position) {
    this.type = type;
    this.value = value;
    this.position = position;
  }

  public TokenType type() {
    return this.type;
  }

  public String value() {
    return this.value;
  }

  public SourcePosition position() {
    return this.position;
  }

  @Override
  public String toString() {
    return String.format("%s[%s]@%s", this.type(), this.value(), this.position());
  }

  public static Token name(String string, SourcePosition range) {
    return new Token(TokenType.NAME, string, range);
  }

  public static Token fragmentSpread(SourcePosition range) {
    return new Token(TokenType.PUNCTUATION, "...", range);
  }

}