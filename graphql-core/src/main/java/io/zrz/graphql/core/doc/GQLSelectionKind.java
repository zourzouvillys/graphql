package io.zrz.graphql.core.doc;

public enum GQLSelectionKind {

  /**
   * a field selection. will implement GQLScalarFieldSelection.
   */

  FIELD,

  /**
   * selection is a reference to another fragment will implement GQLFragmentSpreadSelection
   */

  FRAGMENT_SPREAD,

  /**
   * selection is an inline fragment, will implement GQLInlineFragmentSelection.
   */

  INLINE_FRAGMENT

}
