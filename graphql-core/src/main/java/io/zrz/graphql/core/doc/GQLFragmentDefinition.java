package io.zrz.graphql.core.doc;

import java.util.List;

import io.zrz.graphql.core.types.GQLDeclarationRef;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Singular;
import lombok.ToString;
import lombok.experimental.Wither;

@EqualsAndHashCode
@Wither
@Builder
@ToString
public final class GQLFragmentDefinition implements GQLDefinition
{

  private final String name;
  private final GQLDeclarationRef namedType;

  @Singular
  private final List<GQLDirective> directives;

  @Singular
  private final List<GQLSelection> selections;

  public String name()
  {
    return this.name;
  }

  public GQLDeclarationRef namedType()
  {
    return this.namedType;
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
  public <R> R apply(final GQLDefinitionVisitor<R> visitor)
  {
    return visitor.visitFragment(this);
  }

}
