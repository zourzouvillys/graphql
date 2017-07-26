package io.joss.graphql.core.schema.model;

import io.joss.graphql.core.decl.GQLInputFieldDeclaration;

public class InvalidTypeForInputFieldException extends RuntimeException {

  public InvalidTypeForInputFieldException(InputType inputType, GQLInputFieldDeclaration decl, AbstractType type) {
    super(String.format("Can't use type %s in input field %s.%s", type.getName(), inputType.getName(), decl.name()));
  }

  public InvalidTypeForInputFieldException(Type type) {
    super(String.format("Can't use type %s in input field", type.getName()));
  }

}
