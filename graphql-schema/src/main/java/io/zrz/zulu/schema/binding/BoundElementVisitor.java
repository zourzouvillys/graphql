package io.zrz.zulu.schema.binding;

public interface BoundElementVisitor {

  interface SupplierVisitor<R> {

    R visitOperation(BoundOperation op);

    R visitObject(BoundObjectSelection selection);

    R visitLeaf(BoundLeafSelection leaf);

    R visitFragment(BoundFragment fragment);

  }

}
