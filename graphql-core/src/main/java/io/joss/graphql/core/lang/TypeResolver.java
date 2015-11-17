package io.joss.graphql.core.lang;

import io.joss.graphql.core.decl.GQLDeclaration;
import io.joss.graphql.core.types.GQLDeclarationRef;
import io.joss.graphql.core.types.GQLListType;
import io.joss.graphql.core.types.GQLNonNullType;

public class TypeResolver implements GQLTypeVisitor<GQLDeclaration>
{

  private GQLTypeRegistry typeRegistry;

  public TypeResolver(GQLTypeRegistry typeRegistry)
  {
    this.typeRegistry = typeRegistry;
  }

  @Override
  public GQLDeclaration visitNonNull(GQLNonNullType type)
  {
    throw new RuntimeException();
  }

  @Override
  public GQLDeclaration visitList(GQLListType type)
  {
    throw new RuntimeException();
  }

  @Override
  public GQLDeclaration visitDeclarationRef(GQLDeclarationRef type)
  {
    return typeRegistry.resolve(type);
  }

}
