package io.zrz.zulu.schema.binding;

public interface BoundElement {

  <R> R accept(BoundElementVisitor.SupplierVisitor<R> visitor);

}
