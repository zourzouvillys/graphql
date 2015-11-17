package io.joss.graphql.core.doc;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class GQLSelectedOperation
{

  private final GQLDocument doc;
  private final GQLOperationDefinition query;

  public GQLDocument doc()
  {
    return this.doc;
  }

  public String operationName()
  {
    return this.query.name();
  }

  public GQLOperationDefinition operation()
  {
    return query;
  }

  public static GQLSelectedOperation query(GQLDocument doc, GQLOperationDefinition op)
  {
    if (op == null)
    {
      throw new IllegalArgumentException();
    }
    return new GQLSelectedOperation(doc, op);
  }

  public static GQLSelectedOperation namedQuery(GQLDocument doc, String name)
  {
    if (name == null)
    {
      return defaultQuery(doc);
    }
    return query(doc, doc.named(name));
  }

  public static GQLSelectedOperation defaultQuery(GQLDocument doc)
  {
    return query(doc, doc.operations().iterator().next());
  }

}
