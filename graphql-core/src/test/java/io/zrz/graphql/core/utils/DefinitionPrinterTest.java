package io.zrz.graphql.core.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.junit.Assert;
import org.junit.Test;

import io.zrz.graphql.core.doc.GQLDocument;
import io.zrz.graphql.core.parser.GQLParser;
import io.zrz.graphql.core.parser.GQLSourceInput;

public class DefinitionPrinterTest {

  /**
   * make sure the parse/serialze round trip isn't lossy.
   *
   * @throws IOException
   */

  @Test
  public void testSimple() throws IOException {
    this.checkEquality("query { a }");
  }

  @Test
  public void test() throws IOException {
    this.checkEquality("query { a }");
    this.checkEquality("query { a }");
    this.checkEquality("query { a , b }");
    this.checkEquality("query { a, b { c } }");
    this.checkEquality("query Moo { a, b(a: true) }");
    this.checkEquality("{ a }");
    this.checkEquality("{ a (text: \"in\") }");

  }

  @Test
  public void testAnnotations() throws IOException {
    this.checkEquality("query { id @export(as: \"myname\") @live @bob }");
  }

  public void checkEquality(String input) throws IOException {

    final GQLDocument doc = GQLParser.parseDocument(input, GQLSourceInput.emptySource());

    //
    final ByteArrayOutputStream os = new ByteArrayOutputStream();
    final PrintStream ps = new PrintStream(os);
    new GQLDocumentPrinter().print(doc, ps);
    ps.close();
    os.flush();

    System.err.println(os.toString());

    final GQLDocument doc2 = GQLParser.parseDocument(os.toString(), GQLSourceInput.emptySource());

    Assert.assertEquals(doc.defaultOperation().operationName(), doc2.defaultOperation().operationName());
    Assert.assertEquals(doc.fragments(), doc2.fragments());

  }

}
