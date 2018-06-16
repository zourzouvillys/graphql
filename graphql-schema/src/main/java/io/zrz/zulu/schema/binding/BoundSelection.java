package io.zrz.zulu.schema.binding;

/**
 * a selection item.
 * 
 * @author theo
 *
 */

public interface BoundSelection {

  void apply(VoidVisitor visitor);

  public interface VoidVisitor {

    void apply(BoundLeafSelection sel);

    void apply(BoundObjectSelection sel);

    void apply(BoundInlineFragment sel);

    void apply(BoundNamedFragment sel);

  }

  boolean hasFragmentCycle(BoundNamedFragment frag);

}
