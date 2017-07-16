package io.joss.graphql.core.schema.model;

import lombok.Value;

@Value
public class EnumType implements Type {

  private final String name;

}
