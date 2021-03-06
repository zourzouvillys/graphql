package io.zrz.graphql.core.doc;

import io.zrz.graphql.core.value.GQLObjectValue;

/**
 * A document along with optional named query and input value.
 * 
 * The document isn't validated. Use valid() to determine if the query document and named operation looks reasonable.
 * 
 * @author theo
 *
 * @deprecated use {@link GQLSelectedOperation} instead.
 *
 */

@Deprecated
public class GQLOperationRequest {

  private GQLDocument doc;
  private String query;
  private GQLObjectValue inputs;

  public GQLDocument doc() {
    return this.doc;
  }

  public String operationName() {
    return this.query;
  }

  public GQLObjectValue inputs() {
    return this.inputs;
  }

  public GQLOperationDefinition operation() {
    if (query == null) {
      if (doc().operations().isEmpty()) {
        return null;
      }
      return doc().operations().iterator().next();
    }
    return doc().named(operationName());
  }

  public boolean valid() {
    return operation() != null;
  }

}
