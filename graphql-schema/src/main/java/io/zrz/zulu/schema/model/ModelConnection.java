package io.zrz.zulu.schema.model;

import com.google.common.collect.ImmutableList;

import io.zrz.zulu.schema.binding.BoundObjectSelection;
import io.zrz.zulu.schema.binding.BoundSelection;
import io.zrz.zulu.schema.model.ModelElementVisitor.FunctionVisitor;
import io.zrz.zulu.schema.model.ModelElementVisitor.VoidVisitor;

/**
 * a selection on what looks like a connection.
 * 
 * the node selection will be flattened, so if the selection only contains a single field that field will be returned
 * directly rather than being wrapped in the outer container.
 * 
 * @author theo
 *
 */

public class ModelConnection implements ModelElement {

  private BoundObjectSelection obj;
  private ModelConnectionMeta meta;
  private ImmutableList<BoundSelection> connectionSelections;
  private ImmutableList<BoundSelection> edgeSelections;
  private ImmutableList<ModelElement> nodeSelections;

  public ModelConnection(
      BoundObjectSelection obj,
      ModelConnectionMeta connectionMeta,
      ImmutableList<BoundSelection> connectionSelections,
      ImmutableList<BoundSelection> edgeSelection,
      ImmutableList<ModelElement> nodeSelection) {
    this.obj = obj;
    this.meta = connectionMeta;
    this.connectionSelections = connectionSelections;
    this.edgeSelections = edgeSelection;
    this.nodeSelections = nodeSelection;
  }

  /**
   * the type of the connection field.
   */

  public ModelConnectionMeta connectionMeta() {
    return meta;
  }

  @Override
  public void accept(VoidVisitor visitor) {
    visitor.visitModelConnection(this);
  }

  @Override
  public <T, R> R accept(FunctionVisitor<T, R> visitor, T value) {
    return visitor.visitModelConnection(this, value);
  }

  public ImmutableList<BoundSelection> connectionSelections() {
    return this.connectionSelections;
  }

  public ImmutableList<BoundSelection> edgeSelections() {
    return this.edgeSelections;
  }

  public ImmutableList<ModelElement> nodeSelections() {
    return this.nodeSelections;
  }

  public String fieldName() {
    return this.obj.fieldName();
  }

  public String outputName() {
    return this.obj.outputName();
  }

}
