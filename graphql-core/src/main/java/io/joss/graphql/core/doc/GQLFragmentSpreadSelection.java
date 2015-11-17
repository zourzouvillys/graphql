package io.joss.graphql.core.doc;

import java.util.List;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Singular;
import lombok.ToString;
import lombok.experimental.Wither;

@Wither
@EqualsAndHashCode
@ToString
@Builder(builderClassName = "Builder")
public final class GQLFragmentSpreadSelection implements GQLSelection
{

  /**
   * the name of the fragment this refers to.
   */

  private String name;

  /**
   * any directives.
   */

  @Singular
  private List<GQLDirective> directives;

  public String name()
  {
    return this.name;
  }

  public List<GQLDirective> directives()
  {
    return this.directives;
  }

  @Override
  public <R> R apply(GQLSelectionVisitor<R> visitor)
  {
    return visitor.visitFragmentSelection(this);
  }

}
