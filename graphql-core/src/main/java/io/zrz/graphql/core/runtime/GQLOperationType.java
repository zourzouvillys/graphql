package io.zrz.graphql.core.runtime;

public interface GQLOperationType {

  /**
   * the name of this operation type. for the well known types, these will be normalized to lower case.
   */

  String operationName();

}
