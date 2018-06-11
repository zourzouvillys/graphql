package io.zrz.graphql.core.doc;

import io.zrz.graphql.core.parser.GQLParser;
import io.zrz.graphql.core.parser.GQLSourceInput;

public final class GQLSelectedOperation {

  private final GQLDocument doc;
  private final GQLOperationDefinition query;

  public GQLSelectedOperation(GQLDocument doc, GQLOperationDefinition op) {
    this.doc = doc;
    this.query = op;
  }

  public GQLDocument doc() {
    return this.doc;
  }

  public String operationName() {
    return this.query.name();
  }

  public GQLOperationDefinition operation() {
    return this.query;
  }

  public static GQLSelectedOperation query(GQLDocument doc, GQLOperationDefinition op) {
    if (op == null) {
      throw new IllegalArgumentException();
    }
    return new GQLSelectedOperation(doc, op);
  }

  /**
   * returns the selected operation with the specified named. If the name is null or empty, the default will be
   * returned.
   *
   * If the named query doesn't exist, {@link IllegalArgumentException} is thrown.
   *
   * @param doc
   * @param name
   * @return
   */

  public static GQLSelectedOperation namedQuery(GQLDocument doc, String name) {
    if (name == null || name.isEmpty()) {
      return defaultQuery(doc);
    }
    return query(doc, doc.named(name));
  }

  public static GQLSelectedOperation defaultQuery(GQLDocument doc) {
    final GQLOperationDefinition opdef = doc.operations().iterator().next();
    return query(doc, opdef);
  }

  public static GQLSelectedOperation defaultQuery(String doc) {
    return defaultQuery(GQLParser.parseDocument(doc, GQLSourceInput.emptySource()));
  }

}
