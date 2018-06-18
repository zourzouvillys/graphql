package io.zrz.zulu.schema.model;

public interface ModelElementVisitor {

  public interface VoidVisitor {

    void visitModelRoot(ModelRootType root);

    void visitModelConnection(ModelConnection conn);

    void visitModelObject(ModelObjectType obj);

    void visitModelScalar(ModelScalarField scalar);

  }

  public interface FunctionVisitor<T, R> {

    R visitModelRoot(ModelRootType root, T arg);

    R visitModelConnection(ModelConnection conn, T arg);

    R visitModelObject(ModelObjectType obj, T arg);

    R visitModelScalar(ModelScalarField scalar, T arg);

  }

}
