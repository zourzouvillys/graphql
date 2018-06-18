package io.zrz.zulu.schema;

import java.util.List;

import io.zrz.graphql.core.decl.GQLTypeDeclaration;
import io.zrz.graphql.core.types.GQLTypeDeclKind;

public class ResolvedScalarType extends ResolvedType implements SchemaType, ResolvedOutputType {

  public ResolvedScalarType(SchemaCompiler c, ResolvedSchema s, String t, GQLTypeDeclKind k, List<GQLTypeDeclaration> p) {
    super(c, s, t, k, p);
  }

  @Override
  public void apply(VoidVisitor visitor) {
    visitor.visit(this);
  }

  @Override
  public <R> R apply(SupplierVisitor<R> visitor) {
    return visitor.visit(this);
  }

  @Override
  public <T, R> R apply(FunctionVisitor<T, R> visitor, T value) {
    return visitor.visit(this, value);
  }

  @Override
  public <T1, T2, R> R apply(BiFunctionVisitor<T1, T2, R> visitor, T1 arg1, T2 arg2) {
    return visitor.visit(this, arg1, arg2);
  }

  @Override
  public ResolvedType targetType() {
    return this;
  }

}
