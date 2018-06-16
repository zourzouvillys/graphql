package io.zrz.graphql.core.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Lexer {

  private static final String[] PUNCTUATORS = {
      "...",
      "!",
      "$",
      "(",
      ")",
      ":",
      "=",
      "@",
      "[",
      "]",
      "{",
      "}",
      // for type parsing:
      "|",
      ";",
      "&" };

  public static enum TokenType {

    NAME,

    STRING,

    INT,

    FLOAT,

    COMMENT,

    // very limited sets of token inputs. note that we totally ignore ','.
    PUNCTUATION

  }

  private Token next = null;
  private int pos = 0;
  private final String input;
  private final GQLSourceInput source;

  public Lexer(final String doc, GQLSourceInput source) {
    this.input = doc;
    this.source = source;
  }

  public Token peek() {

    if (this.next != null) {
      return this.next;
    }

    return this.next = this.readToken();

  }

  Token readToken() {

    this.skipWhitespace();

    if (this.pos >= this.input.length()) {
      return null;
    }

    for (final String punctuator : PUNCTUATORS) {
      if (this.is(punctuator)) {
        this.skip(punctuator.length());
        return Token.from(TokenType.PUNCTUATION, punctuator, this.createPosition(this.pos - punctuator.length(), this.pos));
      }
    }

    if (this.is("/*")) {
      // read until */

      final int idx = this.input.indexOf("*/", this.pos);

      if (idx == -1) {
        throw ParserExceptions.endOfStream();
      }

      final String comment = this.input.substring(this.pos, idx + 2);

      try {
        return Token.from(TokenType.COMMENT, comment, this.createPosition(this.pos, idx + 2));
      }
      finally {
        this.pos = idx + 2;
      }

    }

    // int/float.
    if (this.input.charAt(this.pos) == '-' || Character.isDigit(this.input.charAt(this.pos))) {

      for (int i = this.pos; i < this.input.length(); ++i) {

        final char ch = this.input.charAt(i);

        if (Character.isDigit(ch)) {
          // continue
        }
        else if (ch == '-') {
          // it's a float.
        }
        else if (ch == '.') {
          // it's a float.
        }
        else {
          final int oldpos = this.pos;
          final String val = this.input.substring(this.pos, i);
          this.pos = i;
          return Token.from(TokenType.INT, val, this.createPosition(oldpos, this.pos));
        }

      }

      final int oldpos = this.pos;
      final String val = this.input.substring(this.pos, this.input.length());
      this.pos = this.input.length();
      return Token.from(TokenType.INT, val, this.createPosition(oldpos, this.pos));

    }

    if (this.input.charAt(this.pos) == '"' || this.input.charAt(this.pos) == '\'') {

      final char term = this.input.charAt(this.pos);

      final StringBuilder sb = new StringBuilder();

      for (int i = this.pos + 1; i < this.input.length(); ++i) {

        if (this.input.charAt(i) == '\\') {

          if (this.input.charAt(i + 1) == term) {
            ++i;
            sb.append(term);
          }
          else {

            switch (this.input.charAt(i + 1)) {
              case 'n':
                ++i;
                sb.append("\n");
                break;
              case '\\':
                ++i;
                sb.append("\\");
                break;
              default:
                throw new IllegalArgumentException("Illegal escape: \\" + this.input.charAt(i + 1));
            }

          }

        }
        else if (this.input.charAt(i) == term) {
          try {
            return Token.from(TokenType.STRING, sb.toString(), this.createPosition(this.pos + 1, i));
          }
          finally {
            this.pos = i + 1;
          }
        }
        else {
          sb.append(this.input.charAt(i));
        }

      }

      // unterminated.
      throw new IllegalArgumentException("Unterminated string");
    }

    // must be a name(or enum value), right?
    final String text = this.input.substring(this.pos);

    final Pattern comp = Pattern.compile("[_A-Za-z][_0-9A-Za-z]*");

    final Matcher m = comp.matcher(this.input);

    if (!m.find(this.pos) || m.start() != this.pos) {
      throw ParserExceptions.create(this, "Unrecognised input: '" + text + "'");
    }

    this.pos = m.end();

    final String val = this.input.substring(m.start(), m.end());

    return Token.from(TokenType.NAME, val, this.createPosition(m.start(), m.end()));

  }

  private SourcePosition createPosition(final int start, final int end) {
    return new SourcePosition(start, end);
  }

  private void skip(final int length) {
    this.pos += length;
  }

  private boolean is(final String string) {
    if (this.readableChars() < string.length()) {
      return false;
    }
    return this.input.substring(this.pos, this.pos + string.length()).equals(string);
  }

  private int readableChars() {
    return this.input.length() - this.pos;
  }

  // moves forward until the position doesn' contain whitespace.
  private void skipWhitespace() {

    while (this.input.length() > this.pos) {
      final char ch = this.input.charAt(this.pos);
      if (ch == ',') {
        // skip
      }
      else if (ch == '#') {
        // read until end of line.
        final int pos = this.input.indexOf('\n', this.pos);
        if (pos == -1) {
          // no newline.
          this.pos = this.input.length();
        }
        else {
          this.pos = pos;
        }
        continue;
      }
      else if (!Character.isWhitespace(ch)) {
        return;
      }
      ++this.pos;
    }

  }

  public boolean isReadable() {
    // force us to skip whitespace.
    this.peek();
    return this.input.length() > this.pos;
  }

  public Token next() {
    try {
      if (this.next == null) {
        return this.peek();
      }
      return this.next;
    }
    finally {
      this.next = null;
    }
  }

  public boolean hasToken() {
    return this.peek() != null;
  }

  /**
   * converts the input into a list of tokens.
   *
   * @param input
   * @return
   */

  public static final List<Token> tokenize(final String input) {
    return tokenize(input, GQLSourceInput.emptySource());
  }

  public static final List<Token> tokenize(final String input, GQLSourceInput source) {
    final List<Token> tokens = new ArrayList<>();
    final Lexer lex = new Lexer(input, source);
    while (lex.hasToken()) {
      tokens.add(lex.next());
    }
    return tokens;
  }

  public String input() {
    return this.input;
  }

  @org.immutables.value.Value.Immutable(copy = true)
  public static abstract class AbstractLineInfo {

    public abstract GQLSourceInput source();

    public abstract int lineNumber();

    public abstract int lineOffset();

    @Override
    public String toString() {
      return String.format("%s line %d (offset %d)", this.source(), this.lineNumber(), this.lineOffset());
    }

  }

  public ImmutableLineInfo lineNumberAtOffset(int start) {
    int lines = 0;
    int lineStartAt = 0;
    for (int i = 0; i < start; ++i) {
      if (this.input.charAt(i) == '\n') {
        ++lines;
        lineStartAt = i;
      }
    }
    return ImmutableLineInfo.builder()
        .source(this.source)
        .lineNumber(lines + 1)
        .lineOffset(start - lineStartAt)
        .build();
  }

  public GQLSourceLocation position() {
    final ImmutableLineInfo current = this.lineNumberAtOffset(this.pos);
    return ImmutableGQLSourceLocation
        .builder()
        .input(source)
        .sourceOffset(this.pos)
        .lineOffset(this.pos)
        .lineNumber(current.lineNumber())
        .lineOffset(current.lineOffset())
        .build();

  }

}
