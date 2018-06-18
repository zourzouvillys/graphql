package io.zrz.zulu.schema.binding;

import java.util.Collections;
import java.util.List;

import com.google.common.collect.ImmutableList;

import io.zrz.graphql.core.doc.GQLFieldSelection;
import io.zrz.zulu.schema.ResolvedObjectField;
import io.zrz.zulu.schema.ResolvedObjectType;
import io.zrz.zulu.schema.ResolvedType;
import io.zrz.zulu.schema.binding.BoundElementVisitor.SupplierVisitor;

/**
 * a selection on a object field which itself has selections.
 * 
 * @author theo
 *
 */

public class BoundObjectSelection implements BoundSelection, BoundFieldSelection, BoundSelectionContainer {

  /**
   * the child selections. this includes fragments which are constrained by their type.
   */

  private final ImmutableList<BoundSelection> selections;

  private final BoundSelectionContainer parent;
  private final String fieldName;
  private final String outputName;
  private final ResolvedObjectField field;
  private ImmutableList<BoundFieldArgument> params;

  public BoundObjectSelection(BoundSelectionContainer parent, ResolvedObjectField field, GQLFieldSelection sel, BoundBuilder b) {

    this.parent = parent;
    this.fieldName = sel.name();
    this.outputName = sel.outputName();
    this.field = field;

    this.selections = sel.selections()
        .stream()
        .sequential()
        .map(subsel -> BoundUtils.bind(this, subsel, b))
        .collect(ImmutableList.toImmutableList());

    this.params = field.parameters()
        .stream()
        .map(arg -> new BoundFieldArgument(this, arg, sel.args(arg.name())))
        .collect(ImmutableList.toImmutableList());

  }

  public ResolvedObjectField fieldSelection() {
    return this.field;
  }

  /**
   * 
   */

  @Override
  public List<BoundSelection> selections() {
    return this.selections;
  }

  /**
   * the parameters needed for the field.
   */

  public List<BoundFieldArgument> params() {
    return Collections.emptyList(); // this.params;
  }

  @Override
  public ResolvedObjectType type() {
    throw new IllegalArgumentException("");
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
    return selections.stream().anyMatch(sel -> sel.hasFragmentCycle(frag));
  }

  @Override
  public void apply(VoidVisitor visitor) {
    visitor.apply(this);
  }

  @Override
  public ResolvedType selectionType() {
    return this.field.returnType().targetType();
  }

  @Override
  public <R> R accept(SupplierVisitor<R> visitor) {
    return visitor.visitObject(this);
  }

  @Override
  public boolean apply(PredicateVisitor visitor) {
    return visitor.apply(this);
  }

}
