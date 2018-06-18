package io.zrz.zulu.schema;

import java.util.List;

import com.google.common.collect.ImmutableList;

import io.zrz.graphql.core.decl.GQLInputTypeDeclaration;
import io.zrz.graphql.core.types.GQLTypeDeclKind;

public class ResolvedInputType extends ResolvedType implements SchemaType, ResolvedCompositeType {

  private ImmutableList<ResolvedInputField> fields;

  public ResolvedInputType(SchemaCompiler c, ResolvedSchema s, String t, GQLTypeDeclKind k, List<GQLInputTypeDeclaration> p) {
    super(c, s, t, k, p);

    this.fields = p.stream()
        .flatMap(part -> part.fields().stream())
        .map(fdecl -> new ResolvedInputField(this, fdecl, c))
        .collect(ImmutableList.toImmutableList());

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
