package io.zrz.graphql.core.schema;

import java.util.List;

import io.zrz.graphql.core.decl.GQLTypeDeclaration;

public class ResolvedUnit {

  private final List<GQLTypeDeclaration> decls;

  public ResolvedUnit(List<GQLTypeDeclaration> decls) {
    this.decls = decls;
  }

}
