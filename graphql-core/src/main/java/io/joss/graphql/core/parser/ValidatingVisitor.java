package io.joss.graphql.core.parser;

import java.util.HashMap;
import java.util.Map;

import io.joss.graphql.core.doc.GQLDefinition;
import io.joss.graphql.core.doc.GQLDefinitionVisitor;
import io.joss.graphql.core.doc.GQLDocument;
import io.joss.graphql.core.doc.GQLFragmentDefinition;
import io.joss.graphql.core.doc.GQLOpType;
import io.joss.graphql.core.doc.GQLOperationDefinition;

/**
 * validates the basic parts of the document are correct.
 *
 * It does not perform any kind of type or shape validation.
 *
 * @author theo
 *
 */

class ValidatingVisitor implements GQLDefinitionVisitor<GQLDefinition> {

  private GQLOperationDefinition defaultQuery = null;

  private final Map<String, GQLOperationDefinition> operations = new HashMap<>();
  private final Map<String, GQLFragmentDefinition> fragments = new HashMap<>();

  private final GQLDocument doc;

  GQLOperationDefinition defaultQuery() {
    return this.defaultQuery;
  }

  public Map<String, GQLOperationDefinition> operations() {
    return this.operations;
  }

  public Map<String, GQLFragmentDefinition> fragments() {
    return this.fragments;
  }

  public ValidatingVisitor(GQLDocument doc) {
    this.doc = doc;
  }

  @Override
  public GQLDefinition visitOperation(GQLOperationDefinition op) {

    if (op.name() == null) {
      // only a single anonymous query is allowed.
      if (this.defaultQuery != null) {
        throw new RuntimeException("Only a single anonymous query is allowed");
      }
      // anonymous without type specified is a query.
      if (op.type() == null) {
        op = op.withType(GQLOpType.Query);
      }
      this.defaultQuery = op;
    } else if (this.operations.put(op.name(), op) != null) {
      throw new RuntimeException(String.format("operation '%s' defined multiple times", op.name()));
    }

    return op;

  }

  @Override
  public GQLDefinition visitFragment(GQLFragmentDefinition frag) {
    if (this.fragments.put(frag.name(), frag) != null) {
      throw new RuntimeException(String.format("fragment '%s' defined multiple times", frag.name()));
    }
    return frag;
  }

}
