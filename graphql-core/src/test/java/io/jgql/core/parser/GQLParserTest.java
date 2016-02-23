package io.jgql.core.parser;

import static io.joss.graphql.core.doc.GQLArgument.stringArg;
import static io.joss.graphql.core.doc.GQLArgument.trueArg;
import static io.joss.graphql.core.doc.GQLArgument.varArg;
import static io.joss.graphql.core.doc.GQLDirective.createDirective;
import static io.joss.graphql.core.doc.GQLFieldSelection.fieldSelection;
import static io.joss.graphql.core.doc.GQLVariableDefinition.intVar;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.Lists.newArrayList;
import static org.junit.Assert.assertNotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import org.junit.Test;

import io.joss.graphql.core.doc.GQLOpType;
import io.joss.graphql.core.doc.GQLOperationDefinition;
import io.joss.graphql.core.lang.GQLTypeRegistry;
import io.joss.graphql.core.parser.GQLParser;
import io.joss.graphql.core.utils.TypePrinter;

public class GQLParserTest
{

  private final GQLParser PARSER = new GQLParser();

  @Test
  public void testParseQuery()
  {

    final GQLOperationDefinition q = this.PARSER.parseQuery("query A ($input: Int = 1) @if(condition: true) { moo (test: \"hello\") @include(if: $something) }");

    assertNotNull(q);

    assertThat(q.type())
        .isEqualTo(GQLOpType.Query);

    assertThat(q.name())
        .isEqualTo("A");

    assertThat(q.directives())
        .containsExactly(createDirective("if", trueArg("condition")));

    assertThat(q.vars())
        .containsExactly(intVar("input", 1));

    assertThat(q.selections())
        .containsExactly(
            fieldSelection("moo")
                .withDirectives(newArrayList(createDirective("include", varArg("if", "something"))))
                .withArgs(newArrayList(stringArg("test", "hello"))));

  }
  

  @Test
  public void testParseInvalidQuery()
  {

    GQLParser.parseDocument("{}");
    
  }


  @Test
  public void testParseSchema() throws Exception
  {

    GQLTypeRegistry schema = PARSER.parseSchema(streamToString(getClass().getResourceAsStream("/test.schema")));

    schema.types().forEach(type -> {
     
      type.apply(new TypePrinter(System.out));
      System.out.println();
      
    });

  }

  public static String streamToString(final InputStream inputStream) throws Exception
  {
    try (final BufferedReader br = new BufferedReader(new InputStreamReader(inputStream)))
    {
      return br.lines().collect(Collectors.joining("\n"));
    }
    catch (final IOException e)
    {
      throw new RuntimeException(e);
    }
  }

}
