package io.zrz.graphql.generator.java;

import io.zrz.graphql.core.lang.GQLTypeVisitor;
import io.zrz.graphql.core.types.GQLDeclarationRef;
import io.zrz.graphql.core.types.GQLListType;
import io.zrz.graphql.core.types.GQLNonNullType;

public class IsRelayConnectionType implements GQLTypeVisitor<Boolean>
{

  @Override
  public Boolean visitNonNull(GQLNonNullType type)
  {
    return type.type().apply(this);
  }

  @Override
  public Boolean visitList(GQLListType type)
  {
    return type.type().apply(this);
  }

  @Override
  public Boolean visitDeclarationRef(GQLDeclarationRef type)
  {
    return type.name().endsWith("Connection");
  }

}
