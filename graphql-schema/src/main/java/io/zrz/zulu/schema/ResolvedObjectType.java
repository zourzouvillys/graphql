package io.zrz.zulu.schema;

import java.util.List;

import org.eclipse.jdt.annotation.NonNull;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import io.zrz.graphql.core.decl.GQLObjectTypeDeclaration;
import io.zrz.graphql.core.types.GQLTypeDeclKind;

public class ResolvedObjectType extends ResolvedType implements SchemaType, ResolvedObjectOrInterfaceType, ResolvedCompositeType, ResolvedOutputType {

  private ImmutableList<ResolvedObjectField> fields;
  private ImmutableList<SchemaType> interfaces;
  private ImmutableMap<String, ResolvedObjectField> named;

  public ResolvedObjectType(SchemaCompiler c, ResolvedSchema s, String t, GQLTypeDeclKind k, List<GQLObjectTypeDeclaration> p) {

    super(c, s, t, k, p);

    this.interfaces = p.stream()
        .flatMap(part -> part.ifaces().stream())
        .sorted((a, b) -> a.name().compareTo(b.name()))
        .map(arg -> c.build(schema, arg.name()))
        .distinct()
        .collect(ImmutableList.toImmutableList());

    this.fields = p.stream()
        .flatMap(part -> part.fields().stream())
        .map(fdecl -> new ResolvedObjectField(this, fdecl, c))
        .collect(ImmutableList.toImmutableList());

    this.named = this.fields
        .stream()
        .sequential()
        .collect(ImmutableMap.toImmutableMap(e -> e.fieldName(), e -> e));

  }

  public ImmutableList<ResolvedObjectField> fields() {
    return this.fields;
  }

  public ImmutableList<SchemaType> interfaces() {
    return this.interfaces;
  }

  @Override
  public ResolvedObjectField field(@NonNull String fieldName) {
    return this.named.get(fieldName);
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
