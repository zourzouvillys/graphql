package io.zrz.zulu.schema.model;

public interface ModelElement {

  /**
   * 
   * @param visitor
   */

  void accept(ModelElementVisitor.VoidVisitor visitor);

  <T, R> R accept(ModelElementVisitor.FunctionVisitor<T, R> visitor, T value);

}
