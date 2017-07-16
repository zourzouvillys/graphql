package io.joss.graphql.core.schema.model;

import io.joss.graphql.core.decl.GQLSchemaDeclaration;
import io.joss.graphql.core.types.GQLDeclarationRef;
import lombok.Value;

@Value
public class Schema {

  // private String name;
  private final GQLSchemaDeclaration decl;

  public GQLDeclarationRef query() {
    return this.decl.entries().get("query");
  }

  public GQLDeclarationRef mutation() {
    return this.decl.entries().get("mutation");
  }

  public GQLDeclarationRef subscription() {
    return this.decl.entries().get("subscription");
  }

}
