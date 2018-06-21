package io.zrz.graphql.zulu.schema;

import org.eclipse.jdt.annotation.NonNull;

import io.zrz.graphql.zulu.annotations.GQLObjectType;
import io.zrz.graphql.zulu.executable.ExecutableInputField;

@GQLObjectType(name = "__InputValue")
public class GQLSchemaInputValue {

  private final ExecutableInputField field;

  public GQLSchemaInputValue(ExecutableInputField field) {
    this.field = field;
  }

  // name: String!

  public @NonNull String name() {
    return field.fieldName();
  }

  // description: String
  public String description() {
    return null;
  }

  // type: __Type!

  public GQLSchemaType type() {
    if (field.fieldType().type() == null) {
      throw new IllegalStateException();
    }
    return new GQLSchemaType(field.fieldType().type());
  }

  // defaultValue: String
  public String defaultValue() {
    // TODO: output correctly.
    return field.defaultValue().map(val -> val.toString()).orElse(null);
  }

}
