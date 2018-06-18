package io.zrz.zulu.schema.binding;

import java.util.List;

import io.zrz.zulu.schema.ResolvedType;

/**
 * a container of other selections, so always outputs an object.
 *
 * @author theo
 *
 */

public interface BoundSelectionContainer {

  /**
   * the child selections.
   */

  List<BoundSelection> selections();

  /**
   * the type of the container. the child fields will be resolved using this type.
   */

  ResolvedType type();

  /**
   * the type to use for selecting subselections.
   */

  ResolvedType selectionType();

}
