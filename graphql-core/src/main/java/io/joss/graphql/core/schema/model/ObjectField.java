package io.joss.graphql.core.schema.model;

import io.joss.graphql.core.decl.GQLFieldDeclaration;
import io.joss.graphql.core.utils.TypeRefStringGenerator;
import lombok.Value;

@Value
public class ObjectField {

  private GQLFieldDeclaration decl;

  @Override
  public String toString() {
    return String.format("%s: %s", this.decl.name(), this.decl.type().apply(TypeRefStringGenerator.getInstance()));
  }

}
