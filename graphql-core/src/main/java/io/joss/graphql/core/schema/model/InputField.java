package io.joss.graphql.core.schema.model;

import io.joss.graphql.core.decl.GQLFieldDeclaration;
import io.joss.graphql.core.decl.GQLInputFieldDeclaration;
import io.joss.graphql.core.utils.TypeRefStringGenerator;

public class InputField {

  private final InputType inputType;
  private final Model model;
  private final GQLInputFieldDeclaration decl;

  public InputField(InputType inputType, Model model, GQLInputFieldDeclaration decl) {
    this.inputType = inputType;
    this.model = model;
    this.decl = decl;
  }

  @Override
  public String toString() {
    return String.format("%s: %s", this.decl.name(), this.decl.type().apply(TypeRefStringGenerator.getInstance()));
  }

  public GQLFieldDeclaration getDecl() {
    return this.decl;
  }

}
