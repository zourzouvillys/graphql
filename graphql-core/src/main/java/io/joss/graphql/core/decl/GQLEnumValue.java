package io.joss.graphql.core.decl;

import lombok.Builder;
import lombok.ToString;
import lombok.experimental.Wither;

@Wither
@ToString
@Builder
public final class GQLEnumValue {

  private final String name;
  private final String description;
  private final String deprecationReason;

  public String name() {
    return this.name;
  }

  public String description() {
    return this.description;
  }

  public String deprecationReason() {
    return this.deprecationReason;
  }

}
