package io.joss.graphql.core.doc;

import java.util.Arrays;
import java.util.List;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Singular;
import lombok.ToString;
import lombok.experimental.Wither;

@Wither
@EqualsAndHashCode
@ToString
@Builder
public final class GQLDirective
{

  private final String name;

  @Singular
  private final List<GQLArgument> args;

  public String name()
  {
    return this.name;
  }

  public List<GQLArgument> args()
  {
    return this.args;
  }

  public static GQLDirective createDirective(final String name, final GQLArgument... args)
  {
    return builder().name(name).args(Arrays.asList(args)).build();
  }

}
