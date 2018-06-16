package io.zrz.zulu.schema;

import io.zrz.graphql.core.types.GQLTypeDeclKind;
import io.zrz.graphql.core.types.GQLTypeKind;

public interface ResolvedTypeUse {

  boolean isNullable();

  GQLTypeKind typeUseKind();

  GQLTypeDeclKind typeDeclKind();

  ResolvedSchema schema();

  SchemaType targetType();

}
