package io.joss.graphql.core.lang;

import io.joss.graphql.core.types.GQLDeclarationRef;
import io.joss.graphql.core.types.GQLListType;
import io.joss.graphql.core.types.GQLNonNullType;

public interface GQLTypeVisitor<R>
{

  R visitNonNull(GQLNonNullType type);

  R visitList(GQLListType type);

  R visitDeclarationRef(GQLDeclarationRef type);

}
