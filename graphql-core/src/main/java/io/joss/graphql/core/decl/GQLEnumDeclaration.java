package io.joss.graphql.core.decl;

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
public final class GQLEnumDeclaration implements GQLDeclaration
{

  private final String name;
  private final String description;

  @Singular
  private final List<GQLEnumValue> values;

  public String name()
  {
    return this.name;
  }

  public String description()
  {
    return this.description;
  }

  public List<GQLEnumValue> values()
  {
    return this.values;
  }

  @Override
  public <R> R apply(final GQLDeclarationVisitor<R> visitor)
  {
    return visitor.visitEnum(this);
  }

}
