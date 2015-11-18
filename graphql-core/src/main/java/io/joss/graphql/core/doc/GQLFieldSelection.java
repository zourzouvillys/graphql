package io.joss.graphql.core.doc;

import java.util.List;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Singular;
import lombok.ToString;
import lombok.experimental.Wither;

/**
 * A single field selection, which can have arguments, directives, and sub selections.
 * 
 * @author theo
 *
 */

@ToString
@EqualsAndHashCode
@Builder(builderClassName = "Builder")
@Wither
public final class GQLFieldSelection implements GQLSelection
{

  private final String alias;
  private final String name;

  @Singular
  private final List<GQLArgument> args;

  @Singular
  private final List<GQLDirective> directives;

  @Singular
  private final List<GQLSelection> selections;

  public String name()
  {
    return this.name;
  }

  public String alias()
  {
    return this.alias;
  }

  public List<GQLArgument> args()
  {
    return this.args;
  }

  public List<GQLDirective> directives()
  {
    return this.directives;
  }

  public List<GQLSelection> selections()
  {
    return this.selections;
  }

  @Override
  public <R> R apply(GQLSelectionVisitor<R> visitor)
  {
    return visitor.visitFieldSelection(this);
  }

  public static GQLFieldSelection fieldSelection(final String name)
  {
    return builder().name(name).build();
  }

  public GQLArgument args(String name)
  {
    return this.args.stream().filter(a -> a.name().equals(name)).findAny().orElse(null);
  }
  
}
