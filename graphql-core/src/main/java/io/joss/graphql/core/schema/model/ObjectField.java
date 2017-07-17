package io.joss.graphql.core.schema.model;

import io.joss.graphql.core.decl.GQLFieldDeclaration;
import io.joss.graphql.core.decl.GQLParameterableFieldDeclaration;
import io.joss.graphql.core.lang.GQLTypeVisitors;
import io.joss.graphql.core.utils.TypeRefStringGenerator;
import lombok.Getter;

@Getter
public class ObjectField {

  private final Model model;
  private final GQLFieldDeclaration decl;
  private final ObjectType objectType;

  public ObjectField(TypeBuilder builder, ObjectType object, Model model, GQLParameterableFieldDeclaration decl) {
    this.objectType = object;
    builder.lookup(decl.type().apply(GQLTypeVisitors.rootType()).name());
    this.model = model;
    this.decl = decl;
  }

  @Override
  public String toString() {
    return String.format("%s: %s", this.decl.name(), this.decl.type().apply(TypeRefStringGenerator.getInstance()));
  }

}
