package io.zrz.graphql.generator;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

import com.google.common.io.Resources;

import io.zrz.graphql.core.doc.GQLDocument;
import io.zrz.graphql.core.parser.DefaultGQLParser;
import io.zrz.zulu.schema.ResolvedSchema;
import io.zrz.zulu.schema.SchemaCompiler;
import io.zrz.zulu.schema.binding.BoundDocument;
import io.zrz.zulu.schema.binding.BoundDocumentPrinter;

public class ClientGeneratorTest {

  @Test
  public void test() throws IOException {

    // generate schema from file
    SchemaCompiler compiler = new SchemaCompiler();
    compiler.addUnit(Resources.asCharSource(Resources.getResource(getClass(), "/github.schema"), StandardCharsets.UTF_8));
    compiler.addUnit(Resources.asCharSource(Resources.getResource(getClass(), "/introspection.schema"), StandardCharsets.UTF_8));
    ResolvedSchema schema = compiler.compile("Query", "Mutation");

    // parse the query.
    DefaultGQLParser parser = DefaultGQLParser.instance();
    GQLDocument doc = parser.parse(Resources.asCharSource(Resources.getResource(getClass(), "/test.gql"), StandardCharsets.UTF_8).read());

    // bind operations to the schema.

    BoundDocument bdoc = new BoundDocument(schema, doc);

    // bdoc.operations()
    // .forEach(System.err::println);

    new BoundDocumentPrinter(System.out)
        .print(bdoc);

  }

}
