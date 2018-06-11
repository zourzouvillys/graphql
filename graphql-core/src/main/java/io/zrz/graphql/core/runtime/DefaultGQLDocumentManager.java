package io.zrz.graphql.core.runtime;

import io.zrz.graphql.core.doc.GQLDocument;
import io.zrz.graphql.core.parser.DefaultGQLParser;
import io.zrz.graphql.core.parser.GQLParser;

public final class DefaultGQLDocumentManager implements GQLDocumentManager {

  public static final DefaultGQLParser DEFAULT_PARSER = DefaultGQLParser.instance();

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
    return parser.parse(input);
  }

}
