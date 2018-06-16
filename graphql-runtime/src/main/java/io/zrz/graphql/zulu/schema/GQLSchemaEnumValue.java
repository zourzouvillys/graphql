package io.zrz.graphql.zulu.schema;

import io.zrz.graphql.zulu.annotations.GQLOutputType;

@GQLOutputType(name = "__EnumValue")
public class GQLSchemaEnumValue {

  public String name() {
    return null;
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
