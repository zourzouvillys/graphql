package io.zrz.zulu.schema.model;

import io.zrz.zulu.schema.ResolvedType;
import io.zrz.zulu.schema.binding.BoundLeafSelection;
import io.zrz.zulu.schema.model.ModelElementVisitor.FunctionVisitor;
import io.zrz.zulu.schema.model.ModelElementVisitor.VoidVisitor;

/**
 * a field type which is a scalar value.
 * 
 * @author theo
 *
 */

public class ModelScalarField implements ModelElement {

  private BoundLeafSelection leaf;

  public ModelScalarField(BoundLeafSelection leaf) {
    this.leaf = leaf;
  }

  public ResolvedType scalarType() {
    return leaf.fieldType().targetType();
  }

  @Override
  public String toString() {
    return leaf.outputName();
  }

  @Override
  public void accept(VoidVisitor visitor) {
    visitor.visitModelScalar(this);
  }

  @Override
  public <T, R> R accept(FunctionVisitor<T, R> visitor, T value) {
    return visitor.visitModelScalar(this, value);
  }

}
