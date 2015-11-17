package io.joss.graphql.core.doc;

public interface GQLSelectionVisitor<R>
{

  R visitFieldSelection(GQLFieldSelection selection);
  
  R visitFragmentSelection(GQLFragmentSpreadSelection selection);
  
  R visitInlineFragment(GQLInlineFragmentSelection selection);
  
}
