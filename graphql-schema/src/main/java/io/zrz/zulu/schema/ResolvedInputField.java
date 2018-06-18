package io.zrz.zulu.schema;

import io.zrz.graphql.core.decl.GQLInputFieldDeclaration;

public class ResolvedInputField implements SchemaElement {

  private ResolvedInputType enclosing;
  private GQLInputFieldDeclaration fdecl;
  private ResolvedTypeRef fieldType;

  public ResolvedInputField(ResolvedInputType enclosing, GQLInputFieldDeclaration fdecl, SchemaCompiler c) {
    this.enclosing = enclosing;
    this.fdecl = fdecl;
    this.fieldType = c.use(this, fdecl.type());
  }

  @Override
  public ResolvedSchema schema() {
    return this.enclosing.schema();
  }

}
