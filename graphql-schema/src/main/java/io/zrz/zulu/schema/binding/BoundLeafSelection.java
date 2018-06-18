package io.zrz.zulu.schema.binding;

import java.util.Objects;

import com.google.common.base.Preconditions;

import io.zrz.graphql.core.doc.GQLFieldSelection;
import io.zrz.zulu.schema.ResolvedObjectField;
import io.zrz.zulu.schema.ResolvedTypeRef;
import io.zrz.zulu.schema.binding.BoundElementVisitor.SupplierVisitor;

public class BoundLeafSelection implements BoundSelection, BoundFieldSelection {

  private String fieldName;
  private String outputName;
  private ResolvedObjectField field;

  BoundLeafSelection(ResolvedObjectField field, GQLFieldSelection sel, BoundBuilder boundBuilder) {
    Preconditions.checkArgument(sel.selections().isEmpty());
    this.field = Objects.requireNonNull(field, sel.name());
    this.fieldName = sel.name();
    this.outputName = sel.outputName();
  }

  @Override
  public void apply(VoidVisitor visitor) {
    visitor.apply(this);
  }

  public ResolvedTypeRef fieldType() {
    return this.field.returnType();
  }

  public ResolvedObjectField field() {
    return this.field;
  }

  public String fieldName() {
    return this.fieldName;
  }

  @Override
  public String outputName() {
    return this.outputName;
  }

  @Override
  public boolean hasFragmentCycle(BoundNamedFragment frag) {
    return false;
  }

  @Override
  public <R> R accept(SupplierVisitor<R> visitor) {
    return visitor.visitLeaf(this);
  }

  @Override
  public String toString() {
    return this.outputName + ": " + fieldType() + "." + this.fieldName;
  }

  @Override
  public boolean apply(PredicateVisitor visitor) {
    return visitor.apply(this);
  }

}
