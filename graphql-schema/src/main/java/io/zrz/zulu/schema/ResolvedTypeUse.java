package io.zrz.zulu.schema;

import io.zrz.graphql.core.types.GQLTypeDeclKind;
import io.zrz.graphql.core.types.GQLTypeKind;

public interface ResolvedTypeUse {

  /**
   * if this type use is a list. the component type will be another {@link ResolvedTypeUse}, which may itself be another
   * list if they are nested.
   */

  boolean isList();

  /**
   * if this value is nullable. if this is a list, it refers to the list itself rather than the elements in the list.
   */

  boolean isNullable();

  /**
   * 
   */

  GQLTypeKind typeUseKind();

  GQLTypeDeclKind typeDeclKind();

  ResolvedSchema schema();

  SchemaType targetType();

}
