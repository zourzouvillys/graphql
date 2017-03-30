package io.joss.graphql.core.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.junit.Assert;
import org.junit.Test;

import io.joss.graphql.core.doc.GQLDocument;
import io.joss.graphql.core.parser.GQLParser;

public class DefinitionPrinterTest {

	/**
	 * make sure the parse/serialze round trip isn't lossy.
	 * 
	 * @throws IOException
	 */

	@Test
	public void test() throws IOException {
		checkEquality("query { a }");
		checkEquality("query { a }");
		checkEquality("query { a , b }");
		checkEquality("query { a, b { c } }");
		checkEquality("query Moo { a, b(a: true) }");
		checkEquality("{ a }");
		checkEquality("{ a (text: \"in\") }");

	}

	@Test
	public void testAnnotations() throws IOException {
		checkEquality("query { id @export(as: \"myname\") @live @bob }");
	}

	public void checkEquality(String input) throws IOException {

		GQLDocument doc = GQLParser.parseDocument(input);

		//
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(os);
		new GQLDocumentPrinter().print(doc, ps);
		ps.close();
		os.flush();

		System.err.println(os.toString());
		
		
		GQLDocument doc2 = GQLParser.parseDocument(os.toString());

		Assert.assertEquals(doc, doc2);

	}

}
