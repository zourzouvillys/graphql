package io.zrz.graphql.zulu.schema;

import org.eclipse.jdt.annotation.NonNull;

import io.zrz.graphql.zulu.annotations.GQLObjectType;
import io.zrz.graphql.zulu.executable.ExecutableInput;

@GQLObjectType(name = "__InputValue")
public class GQLSchemaInputValue {

  private final ExecutableInput field;

  public GQLSchemaInputValue(final ExecutableInput field) {
    this.field = field;
  }

  // name: String!
  public @NonNull String name() {
    return this.field.fieldName();
  }

  // description: String
  public String description() {
    return null;
  }

  // type: __Type!
  public GQLSchemaType type() {
    if (this.field.fieldType().type() == null) {
      throw new IllegalStateException();
    }
    return new GQLSchemaType(this.field.fieldType().type(), this.field.fieldType().arity(), this.field.isNullable());
  }

  // defaultValue: String
  public String defaultValue() {
    // TODO: output correctly.
    return this.field.defaultValue().map(val -> val.toString()).orElse(null);
  }

}
