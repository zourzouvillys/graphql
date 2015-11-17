package io.joss.graphql.core;

import java.util.List;

import io.joss.graphql.core.decl.GQLArgumentDefinition;
import lombok.Builder;
import lombok.Singular;
import lombok.ToString;

/**
 * Directives are used for both schema and queries.
 * 
 * @author theo
 *
 */

@ToString
@Builder
public final class GQLDirective
{

  private final String name;
  private final String description;

  @Singular
  private final List<GQLArgumentDefinition> args;

  public String name()
  {
    return this.name;
  }

  public String description()
  {
    return this.description;
  }

  public List<GQLArgumentDefinition> args()
  {
    return this.args;
  }

}
