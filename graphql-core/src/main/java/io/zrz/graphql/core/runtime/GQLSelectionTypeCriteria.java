package io.zrz.graphql.core.runtime;

import io.zrz.graphql.core.types.GQLDeclarationRef;

/**
 * a criteria which only matches if the context implements the specified type.
 */

public class GQLSelectionTypeCriteria {

  private GQLDeclarationRef decl;

  public GQLSelectionTypeCriteria(GQLDeclarationRef decl) {
    this.decl = decl;
  }

  /**
   * the provided type name which this spread should be applied,
   */

  public String type() {
    return decl.ref().name();
  }

  @Override
  public String toString() {
    return "on " + this.decl.toString();
  }

}
