package io.zrz.graphql.core.parser;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import io.zrz.graphql.core.decl.GQLDeclaration;
import io.zrz.graphql.core.doc.GQLDocument;
import io.zrz.graphql.core.doc.GQLOperationDefinition;
import io.zrz.graphql.core.doc.GQLSelection;
import io.zrz.graphql.core.doc.ImmutableGQLDocument;
import io.zrz.graphql.core.lang.GQLSchemaBuilder;
import io.zrz.graphql.core.lang.GQLTypeRegistry;
import io.zrz.graphql.core.types.GQLTypeReference;
import io.zrz.graphql.core.types.GQLTypes;
import io.zrz.graphql.core.value.GQLValue;

/**
 * Although a parser generator could be used, the amount of code is pretty much identical due to the well designed
 * nature of the GraphQL specification.
 *
 * @author theo
 *
 */

public class DefaultGQLParser implements GQLParser {

  private static final DefaultGQLParser INSTANCE = new DefaultGQLParser();

  public GQLValue parseValue(final String value, final GQLSourceInput source) {
    final ParseContext ctx = new ParseContext(value, source);
    return ctx.parseValue();
  }

  public static GQLTypeReference parseTypeRef(final String value, final GQLSourceInput source) {
    final ParseContext ctx = new ParseContext(value, source);
    return ctx.parseTypeRef();
  }

  /**
   * Parses the given string into a document model and performs basic structural validation, but doesn't validate the
   * shape or other query semantics.
   *
   * @param doc
   * @return
   *
   */

  @Override
  public GQLDocument parse(final String doc, final GQLSourceInput source) {
    if (doc == null || doc.trim().length() == 0) {
      throw ParserExceptions.endOfStream();
    }
    final ParseContext ctx = new ParseContext(doc, source);
    final GQLDocument mdoc = ctx.parseDocument();
    return this.validate(mdoc);
  }

  /**
   * validates the document, and updates references etc.
   *
   * @param doc
   * @return
   */

  private GQLDocument validate(final GQLDocument doc) {
    final ValidatingVisitor visitor = new ValidatingVisitor(doc);
    return ImmutableGQLDocument.copyOf(doc)
        .withDefinitions(doc.definitions().stream().map(def -> def.apply(visitor)).collect(Collectors.toList()));
  }

  /**
   * Parses a query, which must begin with 'query' or '{'.
   *
   * Note that this is only useful for diagnostics and debugging, as the returned query is not validated and any
   * references to fragments will not be available.
   *
   */

  @Override
  public GQLOperationDefinition parseQuery(final String doc) {
    return this.parseQuery(doc, GQLSourceInput.emptySource());

  }

  public GQLOperationDefinition parseQuery(final String doc, final GQLSourceInput source) {
    final ParseContext ctx = new ParseContext(doc, source);
    final GQLSourceLocation start = ctx.lexer().position();
    return ctx.parseOperation(start);
  }

  /**
   * read the given input string and convert into GQL schema declarations, without performing and processing logic on
   * it.
   *
   * @param schema
   *                 The input string to process
   *
   * @return A list of schema declarations encountered.
   */

  @Override
  public List<GQLDeclaration> readSchema(final String schema, final GQLSourceInput source) {
    return new ParseContext(schema, source).parseSchema();
  }

  public List<GQLDeclaration> readSchema(final InputStream schema, final GQLSourceInput source) {
    return this.readSchema(streamToString(schema), source);
  }

  /**
   * parses a schema definition.
   *
   * @param schema
   * @return
   */

  @Override
  public GQLTypeRegistry parseSchema(final String schema) {
    return this.parseSchema(schema, GQLSourceInput.emptySource());
  }

  @Override
  public GQLTypeRegistry parseSchema(final String schema, final GQLSourceInput source) {
    return new GQLSchemaBuilder()
        .add(GQLTypes.builtins())
        .add(new ParseContext(schema, source).parseSchema())
        .build();
  }

  /**
   * parses a schema definition.
   *
   * @param schema
   * @return
   */

  public GQLTypeRegistry parseSchema(final InputStream schema) {
    return this.parseSchema(schema, GQLSourceInput.emptySource());
  }

  @Override
  public GQLTypeRegistry parseSchema(final InputStream schema, final GQLSourceInput source) {
    return new GQLSchemaBuilder().add(GQLTypes.builtins())
        .add(new ParseContext(streamToString(schema), source).parseSchema())
        .build();
  }

  private static String streamToString(final InputStream inputStream) {
    try (final BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
      return br.lines().collect(Collectors.joining("\n"));
    }
    catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static List<GQLSelection> parseSelection(final String input) {
    final ParseContext ctx = new ParseContext(input, GQLSourceInput.emptySource());
    return ctx.parseSelectionSet();
  }

  public static GQLDocument parseDocument(final String input) {
    return INSTANCE.parse(input, GQLSourceInput.emptySource());
  }

  public static GQLDocument parseDocument(final String input, final GQLSourceInput source) {
    return INSTANCE.parse(input, source);
  }

  public static GQLDocument parseDocument(final InputStream input) {
    return parseDocument(input, GQLSourceInput.emptySource());
  }

  public static GQLDocument parseDocument(final InputStream input, final GQLSourceInput source) {
    if (input == null) {
      throw new IllegalArgumentException("input");
    }
    return parseDocument(streamToString(input), source);
  }

  public static GQLDocument parseDocument(final Path path) {
    try (InputStream in = new FileInputStream(path.toString())) {
      return parseDocument(in, GQLSourceInput.of(path.toString()));
    }
    catch (final Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  public static DefaultGQLParser instance() {
    return INSTANCE;
  }

}
