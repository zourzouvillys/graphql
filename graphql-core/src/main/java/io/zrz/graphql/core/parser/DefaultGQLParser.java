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

  public GQLValue parseValue(String value, GQLSourceInput source) {
    final ParseContext ctx = new ParseContext(value, source);
    return ctx.parseValue();
  }

  public static GQLTypeReference parseTypeRef(String value, GQLSourceInput source) {
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
  public GQLDocument parse(final String doc, GQLSourceInput source) {
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

  private GQLDocument validate(GQLDocument doc) {
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

  public GQLOperationDefinition parseQuery(final String doc, GQLSourceInput source) {
    final ParseContext ctx = new ParseContext(doc, source);
    return ctx.parseOperation();
  }

  /**
   * read the given input string and convert into GQL schema declarations, without performing and processing logic on
   * it.
   *
   * @param schema
   *          The input string to process
   *
   * @return A list of schema declarations encountered.
   */

  public List<GQLDeclaration> readSchema(String schema, GQLSourceInput source) {
    return new ParseContext(schema, source).parseSchema();
  }

  public List<GQLDeclaration> readSchema(InputStream schema, GQLSourceInput source) {
    return this.readSchema(streamToString(schema), source);
  }

  /**
   * parses a schema definition.
   *
   * @param schema
   * @return
   */

  public GQLTypeRegistry parseSchema(String schema) {
    return this.parseSchema(schema, GQLSourceInput.emptySource());
  }

  @Override
  public GQLTypeRegistry parseSchema(String schema, GQLSourceInput source) {
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

  public GQLTypeRegistry parseSchema(InputStream schema) {
    return this.parseSchema(schema, GQLSourceInput.emptySource());
  }

  @Override
  public GQLTypeRegistry parseSchema(InputStream schema, GQLSourceInput source) {
    return new GQLSchemaBuilder().add(GQLTypes.builtins())
        .add(new ParseContext(streamToString(schema), source).parseSchema()).build();
  }

  private static String streamToString(final InputStream inputStream) {
    try (final BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
      return br.lines().collect(Collectors.joining("\n"));
    }
    catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static List<GQLSelection> parseSelection(String input) {
    final ParseContext ctx = new ParseContext(input, GQLSourceInput.emptySource());
    return ctx.parseSelectionSet();
  }

  public static GQLDocument parseDocument(String input) {
    return INSTANCE.parse(input, GQLSourceInput.emptySource());
  }

  public static GQLDocument parseDocument(String input, GQLSourceInput source) {
    return INSTANCE.parse(input, source);
  }

  public static GQLDocument parseDocument(InputStream input) {
    return parseDocument(input, GQLSourceInput.emptySource());
  }

  public static GQLDocument parseDocument(InputStream input, GQLSourceInput source) {
    if (input == null) {
      throw new IllegalArgumentException("input");
    }
    return parseDocument(streamToString(input), source);
  }

  public static GQLDocument parseDocument(Path path) {
    try (InputStream in = new FileInputStream(path.toString())) {
      return parseDocument(in, GQLSourceInput.of(path.toString()));
    }
    catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  public static DefaultGQLParser instance() {
    return INSTANCE;
  }

}
