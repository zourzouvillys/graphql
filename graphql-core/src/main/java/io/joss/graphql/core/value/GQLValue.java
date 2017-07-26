package io.joss.graphql.core.value;

/**
 * An value from GraphQL.
 *
 * Each of the types in the GraphQL world have an implementation of this
 * interface to hold the value, as well as the special {@link GQLVariableRef}
 * which is a reference to a variable.
 *
 */

public interface GQLValue {

  /**
   * type type of this instance.
   */

  GQLValueType type();

  /**
   * Applies this value to the visitor.
   */

  <R> R apply(GQLValueVisitor<R> visitor);

}
