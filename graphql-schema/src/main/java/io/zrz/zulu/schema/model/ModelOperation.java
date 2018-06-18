package io.zrz.zulu.schema.model;

import java.util.Collections;
import java.util.List;

import io.zrz.zulu.schema.binding.BoundOperation;

/**
 * a model of an operation, which hides query document details and presents a normalized model, including replacing
 * relay style [connection] -> edges to a connection.
 * 
 * each stage flattens any child selections which don't contain anything but nested selections. the resulting type is
 * the first selection which returns an array or multiple fields.
 * 
 * @author theo
 *
 */

public class ModelOperation {

  private BoundOperation op;
  private ModelElement elements;

  public ModelOperation(BoundOperation op, ModelElement elements) {
    this.op = op;
    this.elements = elements;
  }

  public String operationName() {
    return op.operationName();
  }

  public List<ModelInput> vars() {
    return Collections.emptyList();
  }

  public ModelElement element() {
    return elements;
  }

}
