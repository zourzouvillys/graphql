package io.zrz.zulu.schema;

import java.util.List;

import io.zrz.graphql.core.decl.GQLTypeDeclaration;
import io.zrz.graphql.core.types.GQLTypeDeclKind;

public class ResolvedScalarType extends ResolvedType implements SchemaType, ResolvedOutputType {

  public ResolvedScalarType(final SchemaCompiler c, final ResolvedSchema s, final String t, final GQLTypeDeclKind k, final List<GQLTypeDeclaration> p) {

    super(c, s, t, k, p);

  }

  @Override
  public void apply(final VoidVisitor visitor) {
    visitor.visit(this);
  }

  @Override
  public <R> R apply(final SupplierVisitor<R> visitor) {
    return visitor.visit(this);
  }

  @Override
  public <T, R> R apply(final FunctionVisitor<T, R> visitor, final T value) {
    return visitor.visit(this, value);
  }

  @Override
  public <T1, T2, R> R apply(final BiFunctionVisitor<T1, T2, R> visitor, final T1 arg1, final T2 arg2) {
    return visitor.visit(this, arg1, arg2);
  }

  @Override
  public ResolvedType targetType() {
    return this;
  }

}
