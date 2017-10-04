package io.zrz.graphql.core.schema.model;

import io.zrz.graphql.core.schema.TypeRefVisitors;
import io.zrz.graphql.core.types.GQLTypeReference;

public interface TypeRef<T extends Type> {

  boolean isNullable();

  public static TypeRef<InputCompatibleType> createInputCompatible(TypeBuilder builder, GQLTypeReference type) {
    return create(builder, type, TypeRefOption.InputCompatible);
  }

  public static <T extends Type> TypeRef<T> create(TypeBuilder builder, GQLTypeReference type, TypeRefOption... options) {
    return type.apply(new TypeRefExtractor<>(builder, options));
  }

  <R> R apply(TypeRefVisitors.GenericTypRefReturnVisitor<T, R> visitor);

  /**
   * For a simpletyperef, returns the type. for a Generic, returns the inner
   * type.
   */

  Type getRawType();

}
