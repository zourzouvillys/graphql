package io.joss.graphql.core.doc;

import io.joss.graphql.core.parser.GQLParser;
import io.joss.graphql.core.parser.GQLSourceInput;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.ToString;

@ToString
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class GQLSelectedOperation {

  private final GQLDocument doc;
  private final GQLOperationDefinition query;

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
   * returns the selected operation with the specified named. If the name is
   * null or empty, the default will be returned.
   *
   * If the named query doesn't exist, {@link IllegalArgumentException} is
   * thrown.
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
    return query(doc, doc.operations().iterator().next());
  }

  public static GQLSelectedOperation defaultQuery(String doc) {
    return defaultQuery(GQLParser.parseDocument(doc, GQLSourceInput.emptySource()));
  }

}
