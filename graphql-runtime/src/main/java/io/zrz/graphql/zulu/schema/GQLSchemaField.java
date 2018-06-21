package io.zrz.graphql.zulu.schema;

import java.util.List;

import org.eclipse.jdt.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import io.zrz.graphql.zulu.annotations.GQLObjectType;
import io.zrz.graphql.zulu.executable.ExecutableOutputField;

@GQLObjectType(name = "__Field")
public class GQLSchemaField {

  private final ExecutableOutputField field;

  public GQLSchemaField(ExecutableOutputField field) {
    this.field = field;
  }

  // name: String!

  public String name() {
    return field.fieldName();
  }

  // description: String
  public String description() {
    return field.documentation();
  }

  // args: [__InputValue!]!

  public @NonNull List<io.zrz.graphql.zulu.schema.GQLSchemaInputValue> args() {
    return field
        .parameters()
        .map(p -> p.fieldValues())
        .orElse(ImmutableList.of())
        .stream()
        .map(x -> new GQLSchemaInputValue(x))
        .collect(ImmutableList.toImmutableList());
  }

  // type: __Type!

  public GQLSchemaType type() {

    if (field.fieldType().arity() > 0) {

      return new GQLSchemaType(field.fieldType().type(), field.fieldType().arity());

    }

    return new GQLSchemaType(field.fieldType().type());
  }

  // isDeprecated: Boolean!

  public boolean isDeprecated() {
    return false;
  }

  // deprecationReason: String

  public String deprecationReason() {
    return null;
  }

}
