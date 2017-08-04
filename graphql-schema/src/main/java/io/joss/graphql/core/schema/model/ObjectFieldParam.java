package io.joss.graphql.core.schema.model;

import io.joss.graphql.core.decl.GQLArgumentDefinition;
import io.joss.graphql.core.value.GQLValue;
import lombok.Getter;

public class ObjectFieldParam {

  private final String name;
  private final TypeRef<InputCompatibleType> typeRef;

  @Getter
  private final GQLValue defaultValue;

  public ObjectFieldParam(TypeBuilder builder, GQLArgumentDefinition arg) {
    this.name = arg.name();
    this.typeRef = TypeRef.createInputCompatible(builder, arg.type());
    this.defaultValue = arg.defaultValue();
  }

  public String getName() {
    return this.name;
  }

  public TypeRef<InputCompatibleType> getType() {
    return this.typeRef;
  }

}
