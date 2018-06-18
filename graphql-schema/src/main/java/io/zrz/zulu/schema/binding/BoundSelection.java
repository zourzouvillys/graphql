package io.zrz.zulu.schema.binding;

/**
 * a selection item.
 * 
 * @author theo
 *
 */

public interface BoundSelection extends BoundElement {

  void apply(VoidVisitor visitor);

  boolean apply(PredicateVisitor visitor);

  /**
   * 
   */

  public interface VoidVisitor {

    void apply(BoundLeafSelection sel);

    void apply(BoundObjectSelection sel);

    void apply(BoundInlineFragment sel);

    void apply(BoundNamedFragment sel);

  }

  public interface PredicateVisitor {

    boolean apply(BoundLeafSelection sel);

    boolean apply(BoundObjectSelection sel);

    boolean apply(BoundInlineFragment sel);

    boolean apply(BoundNamedFragment sel);

  }

  boolean hasFragmentCycle(BoundNamedFragment frag);

}
