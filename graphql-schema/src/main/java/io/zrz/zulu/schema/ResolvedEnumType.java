package io.zrz.zulu.schema;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import io.zrz.graphql.core.decl.GQLEnumDeclaration;
import io.zrz.graphql.core.decl.GQLEnumValue;
import io.zrz.graphql.core.types.GQLTypeDeclKind;

public class ResolvedEnumType extends ResolvedType implements SchemaType, ResolvedOutputType {

  private ImmutableList<GQLEnumValue> ordered;
  private ImmutableSet<GQLEnumValue> values;

  public ResolvedEnumType(SchemaCompiler c, ResolvedSchema s, String t, GQLTypeDeclKind k, List<GQLEnumDeclaration> p) {

    super(c, s, t, k, p);

    this.ordered = p.stream()
        .flatMap(e -> e.values().stream())
        .collect(ImmutableList.toImmutableList());

    this.values = ImmutableSet.copyOf(ordered);

  }

  public ImmutableSet<GQLEnumValue> values() {
    return this.values;
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
