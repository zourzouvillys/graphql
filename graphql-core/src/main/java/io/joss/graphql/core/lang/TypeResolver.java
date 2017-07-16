package io.joss.graphql.core.lang;

import io.joss.graphql.core.decl.GQLTypeDeclaration;
import io.joss.graphql.core.types.GQLDeclarationRef;
import io.joss.graphql.core.types.GQLListType;
import io.joss.graphql.core.types.GQLNonNullType;

public class TypeResolver implements GQLTypeVisitor<GQLTypeDeclaration>
{

  private GQLTypeRegistry typeRegistry;

  public TypeResolver(GQLTypeRegistry typeRegistry)
  {
    this.typeRegistry = typeRegistry;
  }

  @Override
  public GQLTypeDeclaration visitNonNull(GQLNonNullType type)
  {
    throw new RuntimeException();
  }

  @Override
  public GQLTypeDeclaration visitList(GQLListType type)
  {
    throw new RuntimeException();
  }

  @Override
  public GQLTypeDeclaration visitDeclarationRef(GQLDeclarationRef type)
  {
    return typeRegistry.resolve(type);
  }

}
