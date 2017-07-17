package io.joss.graphql.core.schema.model;

import io.joss.graphql.core.decl.GQLInputFieldDeclaration;
import io.joss.graphql.core.value.GQLValue;
import lombok.Getter;

public class InputField {

  @Getter
  private final Model model;

  @Getter
  private final InputType inputType;

  @Getter
  private final TypeRef<InputCompatibleType> fieldType;

  @Getter
  private final GQLValue defaultValue;

  @Getter
  private final String description;
  private final String name;

  public InputField(TypeBuilder builder, InputType inputType, Model model, GQLInputFieldDeclaration decl) {
    this.model = model;
    this.name = decl.name();
    this.inputType = inputType;
    this.defaultValue = decl.defaultValue();
    this.description = decl.description();
    this.fieldType = TypeRef.createInputCompatible(builder, decl.type());
  }

  @Override
  public String toString() {
    return String.format("%s: %s", this.getName(), this.fieldType);
  }

  public String getName() {
    return this.name;
  }

  public TypeRef<InputCompatibleType> getType() {
    return this.fieldType;
  }

}
