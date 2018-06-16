package io.zrz.zulu.schema;

import java.util.List;

import org.eclipse.jdt.annotation.NonNull;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import io.zrz.graphql.core.decl.GQLInterfaceTypeDeclaration;
import io.zrz.graphql.core.types.GQLTypeDeclKind;

public class ResolvedInterfaceType extends ResolvedType implements SchemaType, ResolvedObjectOrInterfaceType {

  private final ImmutableList<ResolvedObjectField> fields;
  private final ImmutableMap<String, ResolvedObjectField> named;

  public ResolvedInterfaceType(SchemaCompiler c, ResolvedSchema s, String t, GQLTypeDeclKind k, List<GQLInterfaceTypeDeclaration> p) {
    super(c, s, t, k, p);

    this.fields = p.stream()
        .flatMap(part -> part.fields().stream())
        .map(fdecl -> new ResolvedObjectField(this, fdecl, c))
        .collect(ImmutableList.toImmutableList());

    this.named = this.fields
        .stream()
        .sequential()
        .collect(ImmutableMap.toImmutableMap(e -> e.fieldName(), e -> e));

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
  public ResolvedObjectField field(@NonNull String fieldName) {
    return this.named.get(fieldName);
  }

  @Override
  public ResolvedType targetType() {
    return this;
  }

}
