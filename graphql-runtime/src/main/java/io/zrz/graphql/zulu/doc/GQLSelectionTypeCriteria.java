package io.zrz.graphql.zulu.doc;

import io.zrz.graphql.core.decl.GQLTypeDeclaration;
import io.zrz.graphql.core.types.GQLDeclarationRef;

/**
 * a criteria which only matches if the context implements the specified type.
 */

public class GQLSelectionTypeCriteria {

  private final GQLDeclarationRef decl;

  public GQLSelectionTypeCriteria(final GQLDeclarationRef decl) {
    this.decl = decl;
  }

  public GQLDeclarationRef refType() {
    return this.decl;
  }

  /**
   * the provided type name which this spread should be applied,
   */

  public String type() {
    final GQLTypeDeclaration ref = this.decl.ref();
    if (ref == null)
      return null;
    return ref.name();
  }

  @Override
  public String toString() {
    return "on " + this.decl.toString();
  }

}
