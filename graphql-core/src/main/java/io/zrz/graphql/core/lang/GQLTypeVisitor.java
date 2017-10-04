package io.zrz.graphql.core.lang;

import io.zrz.graphql.core.types.GQLDeclarationRef;
import io.zrz.graphql.core.types.GQLListType;
import io.zrz.graphql.core.types.GQLNonNullType;

public interface GQLTypeVisitor<R>
{

  R visitNonNull(GQLNonNullType type);

  R visitList(GQLListType type);

  R visitDeclarationRef(GQLDeclarationRef type);

}
