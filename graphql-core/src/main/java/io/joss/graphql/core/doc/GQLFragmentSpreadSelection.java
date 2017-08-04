package io.joss.graphql.core.doc;

import java.util.List;

import io.joss.graphql.core.parser.GQLSourceLocation;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Singular;
import lombok.ToString;
import lombok.experimental.Wither;

@Wither
@EqualsAndHashCode
@ToString
@Builder(builderClassName = "Builder")
public final class GQLFragmentSpreadSelection implements GQLSelection {

  /**
   * the name of the fragment this refers to.
   */

  private final String name;
  private final GQLSourceLocation location;

  /**
   * any directives.
   */

  @Singular
  private final List<GQLDirective> directives;

  public String name() {
    return this.name;
  }

  public List<GQLDirective> directives() {
    return this.directives;
  }

  @Override
  public <R> R apply(GQLSelectionVisitor<R> visitor) {
    return visitor.visitFragmentSelection(this);
  }

  @Override
  public GQLSourceLocation location() {
    return this.location;
  }

}
