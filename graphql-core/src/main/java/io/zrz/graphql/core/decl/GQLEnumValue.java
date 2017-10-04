package io.zrz.graphql.core.decl;

import java.util.List;

import io.zrz.graphql.core.doc.GQLDirective;
import lombok.Builder;
import lombok.Singular;
import lombok.ToString;
import lombok.experimental.Wither;

@Wither
@ToString
@Builder
public final class GQLEnumValue {

  private final String name;
  private final String description;
  private final String deprecationReason;
  @Singular
  private final List<GQLDirective> directives;

  public String name() {
    return this.name;
  }

  public String description() {
    return this.description;
  }

  public String deprecationReason() {
    return this.deprecationReason;
  }

  public List<GQLDirective> directives() {
    return this.directives;
  }

}
