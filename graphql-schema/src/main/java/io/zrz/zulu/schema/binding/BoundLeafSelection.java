package io.zrz.zulu.schema.binding;

import java.util.Objects;

import com.google.common.base.Preconditions;

import io.zrz.graphql.core.doc.GQLFieldSelection;
import io.zrz.zulu.schema.ResolvedObjectField;
import io.zrz.zulu.schema.TypeRef;

public class BoundLeafSelection implements BoundSelection {

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

  public TypeRef fieldType() {
    return this.field.returnType();
  }

  public ResolvedObjectField field() {
    return this.field;
  }

  public String fieldName() {
    return this.fieldName;
  }

  public String outputName() {
    return this.outputName;
  }

  @Override
  public boolean hasFragmentCycle(BoundNamedFragment frag) {
    return false;
  }

}
