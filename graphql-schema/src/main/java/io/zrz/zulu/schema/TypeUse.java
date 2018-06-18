package io.zrz.zulu.schema;

import java.util.Objects;

import io.zrz.graphql.core.types.GQLTypeDeclKind;
import io.zrz.graphql.core.types.GQLTypeKind;

public class TypeUse implements ResolvedTypeUse, ResolvedTypeRef {

  private ResolvedSchema schema;
  private ResolvedType targetType;
  private boolean isNullable;
  private int dims;

  public TypeUse(ResolvedSchema schema, ResolvedType targetType, boolean isNullable, int dims) {
    this.schema = schema;
    this.dims = dims;
    this.isNullable = isNullable;
    this.targetType = Objects.requireNonNull(targetType);
  }

  @Override
  public boolean isNullable() {
    return this.isNullable;
  }

  public int dims() {
    return this.dims;
  }

  @Override
  public ResolvedSchema schema() {
    return this.schema;
  }

  @Override
  public GQLTypeDeclKind typeDeclKind() {
    return this.targetType.typeKind();
  }

  @Override
  public ResolvedType targetType() {
    return this.targetType;
  }

  @Override
  public GQLTypeKind typeUseKind() {
    return targetType.typeKind().toTypeKind();
  }

  @Override
  public String toString() {
    return this.targetType.toString();
  }

  @Override
  public boolean isList() {
    return this.dims > 0;
  }

}
