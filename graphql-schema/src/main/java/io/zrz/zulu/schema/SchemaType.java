package io.zrz.zulu.schema;

import io.zrz.graphql.core.types.GQLTypeDeclKind;

public interface SchemaType {

  GQLTypeDeclKind typeKind();

  void apply(VoidVisitor visitor);

  <R> R apply(SupplierVisitor<R> visitor);

  <T, R> R apply(FunctionVisitor<T, R> visitor, T value);

  <T1, T2, R> R apply(BiFunctionVisitor<T1, T2, R> visitor, T1 arg1, T2 arg2);

  interface VoidVisitor {

    void visit(ResolvedEnumType type);

    void visit(ResolvedInputType type);

    void visit(ResolvedInterfaceType type);

    void visit(ResolvedObjectType type);

    void visit(ResolvedScalarType type);

    void visit(ResolvedUnionType type);

  }

  interface SupplierVisitor<R> {

    R visit(ResolvedEnumType type);

    R visit(ResolvedInputType type);

    R visit(ResolvedInterfaceType type);

    R visit(ResolvedObjectType type);

    R visit(ResolvedScalarType type);

    R visit(ResolvedUnionType type);

  }

  interface FunctionVisitor<T, R> {

    R visit(ResolvedEnumType type, T value);

    R visit(ResolvedInputType type, T value);

    R visit(ResolvedInterfaceType type, T value);

    R visit(ResolvedObjectType type, T value);

    R visit(ResolvedScalarType type, T value);

    R visit(ResolvedUnionType type, T value);

  }

  interface BiFunctionVisitor<T1, T2, R> {

    R visit(ResolvedEnumType type, T1 arg1, T2 arg2);

    R visit(ResolvedInputType type, T1 arg1, T2 arg2);

    R visit(ResolvedInterfaceType type, T1 arg1, T2 arg2);

    R visit(ResolvedObjectType type, T1 arg1, T2 arg2);

    R visit(ResolvedScalarType type, T1 arg1, T2 arg2);

    R visit(ResolvedUnionType type, T1 arg1, T2 arg2);

  }

}
