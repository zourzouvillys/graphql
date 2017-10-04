package io.zrz.graphql.core.lang;

import io.zrz.graphql.core.types.GQLDeclarationRef;
import io.zrz.graphql.core.types.GQLListType;
import io.zrz.graphql.core.types.GQLNonNullType;

public class IsListTypeVisitor implements GQLTypeVisitor<Boolean>
{

  @Override
  public Boolean visitNonNull(GQLNonNullType type)
  {
    return type.type().apply(this);
  }

  @Override
  public Boolean visitList(GQLListType type)
  {
    return true;
  }

  @Override
  public Boolean visitDeclarationRef(GQLDeclarationRef type)
  {
    return false;
  }

}
