package io.joss.graphql.core.schema.model;

import java.util.List;
import java.util.Map;

import io.joss.graphql.core.decl.GQLSchemaDeclaration;
import io.joss.graphql.core.doc.GQLDirective;
import io.joss.graphql.core.types.GQLDeclarationRef;
import lombok.Value;

@Value
public class Schema {

  public static final String QUERY = "query";
  public static final String MUTATION = "mutation";
  public static final String SUBSCRIPTION = "subscription";

  // private String name;
  private final GQLSchemaDeclaration decl;

  public Map<String, GQLDeclarationRef> keys() {
    return this.decl.entries();
  }

  public GQLDeclarationRef value(String key) {
    return this.decl.entries().get(key);
  }

  public List<GQLDirective> directives() {
    return this.decl.directives();
  }

  public boolean hasKey(String key) {
    return this.decl.entries().containsKey(key);
  }

}
