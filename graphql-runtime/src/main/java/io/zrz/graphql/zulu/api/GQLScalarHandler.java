package io.zrz.graphql.zulu.api;

import io.zrz.graphql.core.value.GQLValue;

/**
 * scalar handler.
 *
 * @author theo
 *
 */

public interface GQLScalarHandler<T> {

  /**
   * converts a raw value into this scalar type.
   */

  T readValue(GQLValue value);

}
