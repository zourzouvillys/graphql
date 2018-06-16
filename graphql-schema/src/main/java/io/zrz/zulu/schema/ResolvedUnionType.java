package io.zrz.zulu.schema;

import java.util.List;

import com.google.common.collect.ImmutableSet;

import io.zrz.graphql.core.decl.GQLUnionTypeDeclaration;
import io.zrz.graphql.core.types.GQLTypeDeclKind;

public class ResolvedUnionType extends ResolvedType implements SchemaType, SchemaElement {

  private ImmutableSet<SchemaType> types;

  public ResolvedUnionType(SchemaCompiler c, ResolvedSchema s, String t, GQLTypeDeclKind k, List<GQLUnionTypeDeclaration> p) {
    super(c, s, t, k, p);

    this.types = p.stream()
        .flatMap(part -> part.types().stream())
        .map(type -> c.build(schema, type.name()))
        .collect(ImmutableSet.toImmutableSet());

  }

  public ImmutableSet<SchemaType> types() {
    return this.types;
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
