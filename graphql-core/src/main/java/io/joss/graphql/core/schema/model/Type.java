package io.joss.graphql.core.schema.model;

import io.joss.graphql.core.schema.TypeVisitors;

/**
 * base type for input, type, enum, union and scalar.
 *
 * @author theo
 *
 */

public interface Type {

  String getName();

  <R> R apply(TypeVisitors.GenericReturnVisitor<R> visitor);

  void apply(TypeVisitors.NoReturnVisitor visitor);

}
