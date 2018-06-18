package io.zrz.graphql.generator;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.io.Resources;

import io.zrz.graphql.core.doc.GQLDocument;
import io.zrz.graphql.core.parser.DefaultGQLParser;
import io.zrz.zulu.schema.ResolvedSchema;
import io.zrz.zulu.schema.SchemaCompiler;
import io.zrz.zulu.schema.binding.BoundDocument;
import io.zrz.zulu.schema.binding.BoundElementPrintingVisitor;
import io.zrz.zulu.schema.binding.BoundOperation;
import io.zrz.zulu.schema.model.ModelElementPrinter;
import io.zrz.zulu.schema.model.ModelExtractor;
import io.zrz.zulu.schema.model.ModelOperation;

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

    BoundOperation op = bdoc.operation("repositoryLabels");

    // bdoc.operations()
    // .forEach(System.err::println);

    op.accept(new BoundElementPrintingVisitor(System.out));

    ImmutableList<ModelOperation> roots = bdoc.operations()
        .stream()
        .map(e -> new ModelOperation(e, e.accept(new ModelExtractor())))
        .collect(ImmutableList.toImmutableList());

    ModelOperation root = new ModelOperation(op, op.accept(new ModelExtractor()));

    root.element().accept(new ModelElementPrinter(System.out));

    // new BoundDocumentPrinter(System.out)
    // .print(op);

    // ModelOperation mop = new ModelOperation(op);

    // System.err.println("----------");
    new ClientGenerator().addAll(roots).write(System.err);
    // System.err.println("----------");

    // System.err.println(mop);

  }

}
