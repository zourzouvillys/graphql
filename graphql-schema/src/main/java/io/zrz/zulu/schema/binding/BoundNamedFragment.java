package io.zrz.zulu.schema.binding;

import java.util.List;

import com.google.common.collect.ImmutableList;

import io.zrz.graphql.core.doc.GQLFragmentDefinition;
import io.zrz.zulu.schema.ResolvedType;

public class BoundNamedFragment implements BoundSelectionContainer, BoundFragment {

  private String name;
  private ResolvedType type;
  private ImmutableList<BoundSelection> selections;
  private ImmutableList<BoundNamedFragment> spreadDescendants;

  /**
   * fragment shared between operations in the doc.
   * 
   * @param fragment
   * @param b
   */

  BoundNamedFragment(GQLFragmentDefinition fragment, BoundBuilder b) {

    this.name = fragment.name();

    b.add(this, this.name);

    this.type = b.resolve(fragment.namedType());

    this.selections = fragment.selections()
        .stream()
        .sequential()
        .map(sel -> BoundUtils.bind(this, sel, b))
        .collect(ImmutableList.toImmutableList());

  }

  @Override
  public List<BoundSelection> selections() {
    return this.selections;
  }

  @Override
  public ResolvedType spreadType() {
    return this.type;
  }

  public String name() {
    return this.name;
  }

  @Override
  public void apply(VoidVisitor visitor) {
    visitor.apply(this);
  }

  @Override
  public boolean hasFragmentCycle(BoundNamedFragment frag) {
    if (frag == this) {
      return true;
    }
    if (this.selections == null) {
      throw new IllegalArgumentException("fragment cycle detected on '" + this.name + "'");
    }
    return this.selections
        .stream()
        .anyMatch(sel -> sel.hasFragmentCycle(frag));
  }

  /**
   * may be a type, union or interface.
   */

  @Override
  public ResolvedType type() {
    return this.type;
  }

}
