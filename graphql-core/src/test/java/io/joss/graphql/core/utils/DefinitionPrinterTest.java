package io.joss.graphql.core.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.junit.Assert;
import org.junit.Test;

import io.joss.graphql.core.doc.GQLDocument;
import io.joss.graphql.core.parser.GraphQLParser;

public class DefinitionPrinterTest
{

  /**
   * make sure the parse/serialze round trip isn't lossy.
   * 
   * @throws IOException
   */

  @Test
  public void test() throws IOException
  {

    checkEquality("query {}");
    checkEquality("query { a }");
    checkEquality("query { a , b }");
    checkEquality("query { a, b { c } }");
    checkEquality("query Moo { a, b(a: true) }");
    checkEquality("{}");
    checkEquality("{ a (text: \"in\") }");
    
  }

  public void checkEquality(String input) throws IOException
  {


    GQLDocument doc = GraphQLParser.parseDocument(input);

    //
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    PrintStream ps = new PrintStream(os);
    new GQLDocumentPrinter().print(doc, ps);
    ps.close();
    os.flush();
    
    GQLDocument doc2 = GraphQLParser.parseDocument(os.toString());
    
    Assert.assertEquals(doc, doc2);

  }

}
