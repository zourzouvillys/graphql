package io.zrz.graphql.zulu.doc;

import java.util.Objects;

import io.zrz.graphql.core.doc.GQLDocument;
import io.zrz.graphql.core.parser.DefaultGQLParser;
import io.zrz.graphql.core.parser.GQLParser;

public final class DefaultGQLDocumentManager implements GQLDocumentManager {

  private static final DefaultGQLParser DEFAULT_PARSER = DefaultGQLParser.instance();

  private static final DefaultGQLDocumentManager INSTANCE = new DefaultGQLDocumentManager();

  private final GQLParser parser;

  private final GQLTypeResolver typeResolver;

  DefaultGQLDocumentManager() {
    this(new SimpleGQLTypeResolver(), DEFAULT_PARSER);
  }

  DefaultGQLDocumentManager(GQLTypeResolver typeResolver) {
    this(typeResolver, DEFAULT_PARSER);
  }

  DefaultGQLDocumentManager(GQLTypeResolver typeResolver, GQLParser parser) {
    this.typeResolver = typeResolver;
    this.parser = parser;
  }

  @Override
  public GQLPreparedDocument prepareDocument(GQLDocument doc) {
    return new DefaultGQLPreparedDocument(typeResolver, doc);
  }

  @Override
  public GQLDocument parse(String input) {
    return Objects.requireNonNull(parser.parse(input));
  }

  public static DefaultGQLDocumentManager defaultInstance() {
    return INSTANCE;
  }

}
