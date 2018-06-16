package io.zrz.zulu.schema;

import java.util.List;

import io.zrz.graphql.core.decl.GQLTypeDeclaration;
import io.zrz.graphql.core.types.GQLTypeDeclKind;

public abstract class ResolvedType implements SchemaType, TypeRef {

  final ResolvedSchema schema;
  final String typeName;
  final GQLTypeDeclKind typeKind;

  public ResolvedType(SchemaCompiler compiler, ResolvedSchema schema, String typeName, GQLTypeDeclKind typeKind, List<? extends GQLTypeDeclaration> parts) {

    this.schema = schema;
    this.typeName = typeName;
    this.typeKind = typeKind;

    // register this type, so we can resolve in loops.
    compiler.register(typeName, this);

  }

  @Override
  public GQLTypeDeclKind typeKind() {
    return this.typeKind;
  }

  public ResolvedSchema schema() {
    return this.schema;
  }

  public String typeName() {
    return this.typeName;
  }

  @Override
  public String toString() {
    return this.typeKind + "(" + this.typeName + ")";
  }

}
