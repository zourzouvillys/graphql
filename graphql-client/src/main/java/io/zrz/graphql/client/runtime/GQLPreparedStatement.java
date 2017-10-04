package io.zrz.graphql.client.runtime;

import io.zrz.graphql.core.value.GQLObjectValue;

public interface GQLPreparedStatement
{

  /**
   * Executes a query.
   * 
   * @param named
   * @return
   */

  GQLExecution execute(String named, GQLObjectValue input);

  /**
   * Executes a query.
   * 
   * @param input
   * @return
   */

  default GQLExecution execute(GQLObjectValue input)
  {
    return execute(null, input);
  }

  /**
   * Executes a query.
   * 
   * @param named
   * @return
   */

  default GQLExecution execute(String named)
  {
    return execute(named, null);
  }
  
  /**
   * 
   * @return
   */

  default GQLExecution execute()
  {
    return execute(null, null);
  }

}
