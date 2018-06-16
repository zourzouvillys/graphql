package io.zrz.zulu.schema.binding;

import java.util.List;

import com.google.common.collect.ImmutableList;

import io.zrz.graphql.core.doc.GQLInlineFragmentSelection;
import io.zrz.zulu.schema.ResolvedType;

public class BoundInlineFragment implements BoundSelectionContainer, BoundFragment {

  private final ImmutableList<BoundSelection> selections;
  private final ResolvedType type;

  BoundInlineFragment(GQLInlineFragmentSelection frag, BoundSelectionContainer parent, BoundBuilder b) {

    this.type = b.resolve(frag.typeCondition());

    this.selections = frag.selections()
        .stream()
        .sequential()
        .map(sel -> BoundUtils.bind(this, sel, b))
        .collect(ImmutableList.toImmutableList());

  }

  /**
   * may be a type, union or interface.
   */

  @Override
  public ResolvedType type() {
    return this.type;
  }

  @Override
  public List<BoundSelection> selections() {
    return this.selections;
  }

  @Override
  public ResolvedType spreadType() {
    return this.type;
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
    return this.type;
  }

}
