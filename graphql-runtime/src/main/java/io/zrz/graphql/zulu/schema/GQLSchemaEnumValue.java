package io.zrz.graphql.zulu.schema;

import io.zrz.graphql.zulu.annotations.GQLObjectType;
import io.zrz.graphql.zulu.executable.ExecutableEnumValue;

@GQLObjectType(name = "__EnumValue")
public class GQLSchemaEnumValue {

  private final String name;

  public GQLSchemaEnumValue(final ExecutableEnumValue value) {
    this.name = value.name();
  }

  public String name() {
    return this.name;
  }

  public String description() {
    return null;
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
