package io.zrz.graphql.core.doc;

import java.util.Optional;

import io.zrz.graphql.core.runtime.GQLOperationType;

/**
 * The operation types.
 *
 * @author theo
 *
 */

public enum GQLOpType implements GQLOperationType {

  Query,
  Mutation,
  Subscription;

  @Override
  public String operationName() {
    return name().toLowerCase();
  }

  @Override
  public String toString() {
    return operationName();
  }

  @Override
  public Optional<GQLOpType> standardType() {
    return Optional.of(this);
  }

}
