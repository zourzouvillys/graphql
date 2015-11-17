package io.joss.graphql.core.decl;

import io.joss.graphql.core.types.GQLTypeReference;
import io.joss.graphql.core.value.GQLValue;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.Wither;

/**
 * An argument definition.
 * 
 * @author theo
 */

@Wither
@ToString
@EqualsAndHashCode
@Builder(builderClassName = "Builder")
public final class GQLArgumentDefinition
{

  @NonNull
  private final String name;
  private final String description;

  @NonNull
  private final GQLTypeReference type;

  private final GQLValue defaultValue;

  public String name()
  {
    return this.name;
  }

  public String description()
  {
    return this.description;
  }

  public GQLTypeReference type()
  {
    return this.type;
  }

  public GQLValue defaultValue()
  {
    return this.defaultValue;
  }

}
