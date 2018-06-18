package io.zrz.zulu.schema.binding;

import java.util.List;

import org.eclipse.jdt.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import io.zrz.graphql.core.doc.GQLOpType;
import io.zrz.graphql.core.doc.GQLOperationDefinition;
import io.zrz.graphql.core.runtime.GQLOperationType;
import io.zrz.zulu.schema.ResolvedObjectType;
import io.zrz.zulu.schema.binding.BoundElementVisitor.SupplierVisitor;

public class BoundOperation implements BoundSelectionContainer, BoundElement {

  private final BoundDocument doc;
  private final @Nullable String name;
  private final ImmutableList<BoundVariable> vars;
  private final @Nullable GQLOpType type;
  private final ImmutableList<BoundSelection> selections;
  private final ResolvedObjectType rootType;

  public BoundOperation(BoundDocument doc, GQLOperationDefinition op, BoundBuilder b) {

    this.rootType = (ResolvedObjectType) b.schema().operationType(op.type());

    if (this.rootType == null) {
      throw new IllegalArgumentException("can't find root type for '" + op.type() + "' in schema");
    }

    this.doc = doc;
    this.name = op.name();
    this.type = op.type();

    this.vars = op.vars()
        .stream()
        .map(var -> new BoundVariable(this, var, b))
        .collect(ImmutableList.toImmutableList());

    this.selections = op.selections()
        .stream()
        .sequential()
        .map(sel -> BoundUtils.bind(this, sel, b))
        .collect(ImmutableList.toImmutableList());

  }

  public BoundDocument doc() {
    return this.doc;
  }

  public List<BoundVariable> vars() {
    return this.vars;
  }

  public String operationName() {
    return this.name;
  }

  public GQLOperationType operationType() {
    return this.type;
  }

  /**
   * the resolved type for this context.
   */

  @Override
  public ResolvedObjectType type() {
    return this.rootType;
  }

  @Override
  public List<BoundSelection> selections() {
    return this.selections;
  }

  @Override
  public String toString() {
    return type + " " + name;
  }

  @Override
  public ResolvedObjectType selectionType() {
    return this.rootType;
  }

  @Override
  public <R> R accept(SupplierVisitor<R> visitor) {
    return visitor.visitOperation(this);
  }

}
