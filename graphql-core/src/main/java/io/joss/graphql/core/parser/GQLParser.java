package io.joss.graphql.core.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import io.joss.graphql.core.doc.GQLDocument;
import io.joss.graphql.core.doc.GQLOperationDefinition;
import io.joss.graphql.core.lang.GQLSchemaBuilder;
import io.joss.graphql.core.lang.GQLTypeRegistry;
import io.joss.graphql.core.types.GQLTypes;
import io.joss.graphql.core.value.GQLValue;

/**
 * Although a parser generator could be used, the amount of code is pretty much
 * identical due to the well designed nature of the GraphQL specification.
 *
 * @author theo
 *
 */

public class GQLParser {

	public GQLValue parseValue(String value) {
		final ParseContext ctx = new ParseContext(value);
		return ctx.parseValue();
	}

	/**
	 * Parses the given string into a document model and performs basic
	 * structural validation, but doesn't validate the shape or other query
	 * semantics.
	 *
	 * @param doc
	 * @return
	 *
	 */

	public GQLDocument parse(final String doc) {
		if (doc == null || doc.trim().length() == 0) {
			throw ParserExceptions.endOfStream();
		}
		final ParseContext ctx = new ParseContext(doc);
		GQLDocument mdoc = ctx.parseDocument();
		return validate(mdoc);
	}

	/**
	 * validates the document, and updates references etc.
	 * 
	 * @param doc
	 * @return
	 */

	private GQLDocument validate(GQLDocument doc) {
		ValidatingVisitor visitor = new ValidatingVisitor(doc);
		return doc.withDefinitions(
				doc.definitions().stream().map(def -> def.apply(visitor)).collect(Collectors.toList()));
	}

	/**
	 * Parses a query, which must begin with 'query' or '{'.
	 * 
	 * Note that this is only useful for diagnostics and debugging, as the
	 * returned query is not validated and any references to fragments will not
	 * be available.
	 * 
	 */

	public GQLOperationDefinition parseQuery(final String doc) {
		final ParseContext ctx = new ParseContext(doc);
		return ctx.parseOperation();
	}

	/**
	 * parses a schema definition.
	 * 
	 * @param schema
	 * @return
	 */

	public GQLTypeRegistry parseSchema(String schema) {
		return new GQLSchemaBuilder()
				.add(GQLTypes.builtins())
				.add(new ParseContext(schema).parseSchema())
				.build();
	}

	/**
	 * parses a schema definition.
	 * 
	 * @param schema
	 * @return
	 */

	public GQLTypeRegistry parseSchema(InputStream schema) {
		return new GQLSchemaBuilder().add(GQLTypes.builtins())
				.add(new ParseContext(streamToString(schema)).parseSchema()).build();
	}

	private static String streamToString(final InputStream inputStream) {
		try (final BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
			return br.lines().collect(Collectors.joining("\n"));
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static GQLDocument parseDocument(String input) {
		return new GQLParser().parse(input);
	}

	public static GQLDocument parseDocument(InputStream input) {
		if (input == null)
			throw new IllegalArgumentException("input");
		return parseDocument(streamToString(input));
	}

}
