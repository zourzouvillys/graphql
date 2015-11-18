package io.jgql.core.parser;

import org.junit.Test;

import io.joss.graphql.core.doc.GQLDocument;
import io.joss.graphql.core.parser.GQLParser;

public class ParseContextTest
{

  private final GQLParser PARSER = new GQLParser();

  @Test(expected = IllegalArgumentException.class)
  public void testFail()
  {
    this.PARSER.parse("fragment on on Moo {}");
  }

  @Test
  public void test()
  {
    
    this.parse("fragment A on Moo {}");
    this.parse("fragment B on Moo { cows { moo }, meep(field: 1), cows: mooo { again { me } } }");
    this.parse("fragment C on Moo {}");
    this.parse("fragment D on Moo { alias: xxx } fragment F on Cows {}");
    this.parse("   {}   ");
    this.parse(" query myquery {} ");
    this.parse(" query myquery @include(if: $condition) {} ");
    this.parse("query myquery ($condition: Boolean, $another: xxx) {} ");
    this.parse("query myquery ($condition: Boolean = false) {} ");
    this.parse("query myquery ($condition: Boolean = \"xxx\") {} ");
    this.parse("query myquery ($condition: Boolean = 1234) {} ");
    this.parse("query myquery ($condition: Boolean = []) {} ");
    this.parse("query myquery ($condition: Boolean = {}) {} ");
    this.parse("{ some(data: cows) }");

    this.parse("query getZuckProfile($devicePicSize: Int) { user(id: 4) { id name profilePic(size: $devicePicSize) } }");

  }

  private GQLDocument parse(final String string)
  {
    return this.PARSER.parse(string);
  }

}
