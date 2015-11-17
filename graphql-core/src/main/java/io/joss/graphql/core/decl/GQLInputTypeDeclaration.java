package io.joss.graphql.core.decl;

import java.util.List;

import lombok.Builder;
import lombok.Singular;
import lombok.ToString;
import lombok.experimental.Wither;

@ToString
@Wither
@Builder(builderClassName = "Builder")
public class GQLInputTypeDeclaration implements GQLDeclaration
{

  private final String name;
  private final String description;

  @Singular
  private final List<GQLInputFieldDeclaration> fields;

  public static class Builder
  {
  }

  @Override
  public String name()
  {
    return this.name;
  }

  public String description()
  {
    return this.description;
  }

  public List<GQLInputFieldDeclaration> fields()
  {
    return this.fields;
  }

  @Override
  public <R> R apply(GQLDeclarationVisitor<R> visitor)
  {
    return visitor.visitInput(this);
  }

}
