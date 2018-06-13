package io.zrz.graphql.zulu;

/**
 * a representation of a single GraphQL view which has a query, mutation, and subscription root as well as a set of
 * types visible to it.
 * 
 * @author theo
 *
 */

public interface ZuluView {

  /**
   * the root type which is used for queries.
   */

  ZOutputType queryRoot();

  /**
   * the root type used for mutations
   */

  ZOutputType mutationRoot();

}
