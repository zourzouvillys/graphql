package io.zrz.graphql.core.parser;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;

import io.zrz.graphql.core.parser.Lexer;
import io.zrz.graphql.core.parser.SourcePosition;
import io.zrz.graphql.core.parser.Token;

public class LexerTest
{

  @Test
  public void test()
  {

    List<Token> toks = Lexer.tokenize(" ... fragment on moo {  moo: string, cows = \"xxx\" }  ");
    
    assertThat(toks.remove(0)).isEqualTo(Token.fragmentSpread(SourcePosition.range(1, 4)));
    assertThat(toks.remove(0)).isEqualTo(Token.name("fragment", SourcePosition.range(5, 13)));
    assertThat(toks.remove(0)).isEqualTo(Token.name("on", SourcePosition.range(14, 16)));
    
  }

  @Test
  public void testSingleQuoteString()
  {
    Lexer.tokenize("query A { __typename, pbx(domain: 'test') { id } }");
    // .forEach(l -> System.err.println(l));
  }

  @Test
  public void testLex()
  {
    Lexer.tokenize("fragment A on Moo {}");
  }

  @Test
  public void testNumbers()
  {
    Lexer.tokenize("-1");
  }

  @Test
  public void testNumbers2()
  {
    Lexer.tokenize("1.3344 moo");
  }

  @Test
  public void testNumbers3()
  {
    Lexer.tokenize(" query myquery ($condition: Boolean = 1234) {}  ");
  }

}
