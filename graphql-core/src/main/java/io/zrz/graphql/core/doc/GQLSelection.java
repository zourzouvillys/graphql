package io.zrz.graphql.core.doc;

import java.util.Optional;

import io.zrz.graphql.core.parser.GQLSourceLocation;

/**
 * a field selection, inline fragment, or reference to another fragment.
 * 
 * @author theo
 *
 */

public interface GQLSelection {

  GQLSelectionKind selectionKind();

  Optional<GQLSourceLocation> location();

  GQLSelection withLocation(GQLSourceLocation location);

  void apply(VoidVisitor visitor);

  //
  // --- visitors ---
  //

  <R> R apply(GQLSelectionVisitor<R> visitor);

  <T, R> R apply(FunctionVisitor<T, R> visitor, T value);

  <T> void apply(ConsumerVisitor<T> visitor, T value);

  /**
   * visitor which returns no value
   */

  public interface VoidVisitor {

    void visitFieldSelection(GQLFieldSelection field);

    void visitFragmentSelection(GQLFragmentSpreadSelection frag);

    void visitInlineFragment(GQLInlineFragmentSelection inline);

  }

  /**
   * visitor which takes a single value, returning nothing
   */

  public interface ConsumerVisitor<T> {

    void visitFieldSelection(GQLFieldSelection field, T value);

    void visitFragmentSelection(GQLFragmentSpreadSelection frag, T value);

    void visitInlineFragment(GQLInlineFragmentSelection inline, T value);

  }

  /**
   * takes a single value, returns a value.
   */

  public interface FunctionVisitor<T, R> {

    R visitFieldSelection(GQLFieldSelection field, T value);

    R visitFragmentSelection(GQLFragmentSpreadSelection frag, T value);

    R visitInlineFragment(GQLInlineFragmentSelection inline, T value);

  }

}
