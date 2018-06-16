package io.zrz.graphql.zulu.engine;

public interface ZuluSelectionVisitor {

  interface VoidVisitor {

    void accept(ZuluLeafSelection leaf);

    void accept(ZuluContainerSelection container);

  }

  interface SupplierVisitor<R> {

    R accept(ZuluLeafSelection leaf);

    R accept(ZuluContainerSelection container);

  }

  interface ConsumerVisitor<T> {

    void accept(ZuluLeafSelection leaf, T value);

    void accept(ZuluContainerSelection container, T value);

  }

  interface FunctionVisitor<T, R> {

    R accept(ZuluLeafSelection leaf, T value);

    R accept(ZuluContainerSelection container, T value);

  }

  interface OperatorVisitor<U> extends FunctionVisitor<U, U> {

  }

}
