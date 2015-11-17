package io.joss.graphql.core.decl;

import java.util.List;

import io.joss.graphql.core.types.GQLDeclarationRef;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Singular;
import lombok.ToString;
import lombok.experimental.Wither;

@EqualsAndHashCode
@Wither
@ToString
@Builder(builderClassName = "Builder")
public final class GQLUnionTypeDeclaration implements GQLDeclaration
{

  private final String name;
  private final String description;

  @Singular
  private final List<GQLDeclarationRef> types;

  @Override
  public String name()
  {
    return this.name;
  }

  public String description()
  {
    return this.description;
  }

  public List<GQLDeclarationRef> types()
  {
    return this.types;
  }

  @Override
  public <R> R apply(final GQLDeclarationVisitor<R> visitor)
  {
    return visitor.visitUnion(this);
  }

}
