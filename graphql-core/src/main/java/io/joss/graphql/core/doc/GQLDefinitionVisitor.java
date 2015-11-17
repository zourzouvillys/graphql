package io.joss.graphql.core.doc;

public interface GQLDefinitionVisitor<R>
{

  /**
   * An operation.
   */

  R visitOperation(GQLOperationDefinition op);

  /**
   * A fragment.
   */

  R visitFragment(GQLFragmentDefinition frag);

}
