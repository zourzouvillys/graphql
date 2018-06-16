package io.zrz.zulu.schema.binding;

import java.util.List;

import io.zrz.zulu.schema.ResolvedType;

public interface BoundSelectionContainer {

  List<BoundSelection> selections();

  ResolvedType type();

  /**
   * the type to use for selecting subselections.
   */

  ResolvedType selectionType();

}
