package io.zrz.graphql.core.runtime;

import java.util.Optional;

import io.zrz.graphql.core.doc.GQLOpType;

public interface GQLOperationType {

  /**
   * the name of this operation type. for the well known types, these will be normalized to lower case.
   */

  String operationName();

  /**
   * if this operation is one of the standard types returns it, else empty.
   */

  default Optional<GQLOpType> standardType() {
    return Optional.empty();
  }

}
