package io.joss.graphql.core.value;

public interface GQLValue
{

  /**
   * Applies this value to the visitor.
   */

  <R> R apply(GQLValueVisitor<R> visitor);

}
