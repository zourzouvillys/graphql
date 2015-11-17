package io.joss.graphql.generator.java;

import io.joss.graphql.core.lang.GQLTypeVisitor;
import io.joss.graphql.core.types.GQLDeclarationRef;
import io.joss.graphql.core.types.GQLListType;
import io.joss.graphql.core.types.GQLNonNullType;

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
