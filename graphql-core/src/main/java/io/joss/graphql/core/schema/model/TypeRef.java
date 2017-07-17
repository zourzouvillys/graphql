package io.joss.graphql.core.schema.model;

import io.joss.graphql.core.schema.TypeRefVisitors;
import io.joss.graphql.core.types.GQLTypeReference;

public interface TypeRef<T extends Type> {

  public static TypeRef<InputCompatibleType> createInputCompatible(TypeBuilder builder, GQLTypeReference type) {
    return create(builder, type, TypeRefOption.InputCompatible);
  }

  public static <T extends Type> TypeRef<T> create(TypeBuilder builder, GQLTypeReference type, TypeRefOption... options) {
    return type.apply(new TypeRefExtractor<>(builder, options));
  }

  <R> R apply(TypeRefVisitors.GenericTypRefReturnVisitor<T, R> visitor);

}
