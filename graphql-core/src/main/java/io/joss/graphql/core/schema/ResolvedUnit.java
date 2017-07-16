package io.joss.graphql.core.schema;

import java.util.List;

import io.joss.graphql.core.decl.GQLTypeDeclaration;

public class ResolvedUnit {

  private final List<GQLTypeDeclaration> decls;

  public ResolvedUnit(List<GQLTypeDeclaration> decls) {
    this.decls = decls;
  }

}
