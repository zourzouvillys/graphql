package io.joss.graphql.core.schema.model;

import io.joss.graphql.core.decl.GQLArgumentDefinition;

public class ObjectFieldParam {

  private final String name;
  private final TypeRef<InputCompatibleType> typeRef;

  public ObjectFieldParam(TypeBuilder builder, GQLArgumentDefinition arg) {
    this.name = arg.name();
    this.typeRef = TypeRef.createInputCompatible(builder, arg.type());
  }

  public String getName() {
    return this.name;
  }

  public TypeRef<InputCompatibleType> getType() {
    return this.typeRef;
  }

}
