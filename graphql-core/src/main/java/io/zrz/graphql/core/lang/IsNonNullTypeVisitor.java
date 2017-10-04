package io.zrz.graphql.core.lang;

import io.zrz.graphql.core.types.GQLDeclarationRef;
import io.zrz.graphql.core.types.GQLListType;
import io.zrz.graphql.core.types.GQLNonNullType;

public class IsNonNullTypeVisitor implements GQLTypeVisitor<Boolean>
{

  @Override
  public Boolean visitNonNull(GQLNonNullType type)
  {
    return true;
  }

  @Override
  public Boolean visitList(GQLListType type)
  {
    return false;
  }

  @Override
  public Boolean visitDeclarationRef(GQLDeclarationRef type)
  {
    return false;
  }

}
