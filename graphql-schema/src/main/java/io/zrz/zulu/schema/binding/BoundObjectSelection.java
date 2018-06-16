package io.zrz.zulu.schema.binding;

import java.util.List;

import com.google.common.collect.ImmutableList;

import io.zrz.graphql.core.doc.GQLFieldSelection;
import io.zrz.zulu.schema.ResolvedObjectField;
import io.zrz.zulu.schema.ResolvedObjectType;
import io.zrz.zulu.schema.ResolvedType;

/**
 * a selection on a parent object which itself has selections.
 * 
 * @author theo
 *
 */

public class BoundObjectSelection implements BoundSelection, BoundSelectionContainer {

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

  @Override
  public List<BoundSelection> selections() {
    return this.selections;
  }

  public List<BoundFieldArgument> params() {
    return this.params;
  }

  @Override
  public ResolvedObjectType type() {
    throw new IllegalArgumentException("");
  }

  public String fieldName() {
    return this.fieldName;
  }

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

}
