package io.zrz.graphql.core.parser;

import org.immutables.value.Value;

import io.zrz.graphql.core.parser.Lexer.TokenType;

@Value.Immutable
public abstract class Token {

  @Value.Parameter
  public abstract TokenType type();

  @Value.Parameter
  public abstract String value();

  @Value.Parameter
  public abstract SourcePosition position();

  @Override
  public String toString() {
    return String.format("%s[%s]@%s", this.type(), this.value(), this.position());
  }

  public static Token from(TokenType type, String string, SourcePosition range) {
    return ImmutableToken.of(type, string, range);
  }

  public static Token name(String string, SourcePosition range) {
    return ImmutableToken.of(TokenType.NAME, string, range);
  }

  public static Token fragmentSpread(SourcePosition range) {
    return ImmutableToken.of(TokenType.PUNCTUATION, "...", range);
  }

}
