package io.joss.graphql.core.decl;

import io.joss.graphql.core.types.GQLTypeReference;
import lombok.Builder;
import lombok.experimental.Wither;

@Wither
@Builder(builderClassName = "Builder")
public class GQLInputFieldDeclaration implements GQLFieldDeclaration
{

  private final String name;
  private final String description;

  private final GQLTypeReference type;

  private final String deprecationReason;

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

  public String deprecationReason()
  {
    return this.deprecationReason;
  }

  @Override
  public String toString()
  {
    final StringBuilder sb = new StringBuilder("GQLInputField(");
    sb.append(this.name()).append(": ").append(this.type());
    return sb.append(')').toString();

  }

}
