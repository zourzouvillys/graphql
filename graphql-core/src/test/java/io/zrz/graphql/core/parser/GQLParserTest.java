package io.zrz.graphql.core.parser;

import static io.zrz.graphql.core.doc.GQLArgument.stringArg;
import static io.zrz.graphql.core.doc.GQLArgument.trueArg;
import static io.zrz.graphql.core.doc.GQLArgument.varArg;
import static io.zrz.graphql.core.doc.GQLDirective.createDirective;
import static io.zrz.graphql.core.doc.GQLFieldSelection.fieldSelection;
import static io.zrz.graphql.core.doc.GQLVariableDefinition.intVar;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import org.junit.Test;

import io.zrz.graphql.core.doc.GQLOpType;
import io.zrz.graphql.core.doc.GQLOperationDefinition;
import io.zrz.graphql.core.lang.GQLTypeRegistry;
import io.zrz.graphql.core.utils.TypePrinter;

public class GQLParserTest {

  private final GQLParser PARSER = DefaultGQLParser.instance();

  @Test
  public void testParseQuery() {

    final GQLOperationDefinition q = this.PARSER
        .parseQuery("query A ($input: Int = 1) @if(condition: true) { moo (test: \"hello\") @include(if: $something) }");

    assertNotNull(q);

    assertThat(q.type())
        .isEqualTo(GQLOpType.Query);

    assertThat(q.name())
        .isEqualTo("A");

    assertThat(q.directives())
        .containsExactly(createDirective("if", trueArg("condition")));

    assertThat(q.vars())
        .containsExactly(intVar("input", 1));

    assertThat(q.selections().stream().map(x -> x.withLocation(null)).collect(Collectors.toList()))
        .containsExactly(
            fieldSelection("moo")
                .withDirectives(createDirective("include", varArg("if", "something")))
                .withArgs(stringArg("test", "hello")).withLocation(null));

  }

  @Test
  public void testParseInvalidQuery() {

    DefaultGQLParser.parseDocument("{ d }", GQLSourceInput.emptySource());

  }

  @Test
  public void testParseSchema() throws Exception {

    String input = streamToString(this.getClass().getResourceAsStream("/test.schema"));

    final GQLTypeRegistry schema = PARSER.parseSchema(input, GQLSourceInput.emptySource());

    schema.types().forEach(type -> {

      type.apply(new TypePrinter(System.out));
      System.out.println();

    });

  }

  public static String streamToString(final InputStream inputStream) throws Exception {
    try (final BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
      return br.lines().collect(Collectors.joining("\n"));
    }
    catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

}
