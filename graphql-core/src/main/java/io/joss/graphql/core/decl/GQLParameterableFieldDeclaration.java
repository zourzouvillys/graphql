package io.joss.graphql.core.decl;

import java.util.List;

import io.joss.graphql.core.types.GQLTypeReference;
import lombok.Builder;
import lombok.Singular;
import lombok.experimental.Wither;

@Wither
@Builder(builderClassName = "Builder")
public final class GQLParameterableFieldDeclaration implements GQLFieldDeclaration
{

  private final String name;
  private final String description;

  private final GQLTypeReference type;

  private final String deprecationReason;

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

  public GQLTypeReference type()
  {
    return this.type;
  }

  public String deprecationReason()
  {
    return this.deprecationReason;
  }


  public List<GQLArgumentDefinition> args()
  {
    return this.args;
  }

  @Override
  public String toString()
  {
    final StringBuilder sb = new StringBuilder("GQLField(");
    sb.append(this.name()).append(": ").append(this.type());
    return sb.append(')').toString();

  }

  public GQLArgumentDefinition arg(String name)
  {
    return this.args.stream().filter(arg -> name.equals(arg.name())).findAny().orElse(null);
  }

}
