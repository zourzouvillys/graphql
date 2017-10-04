package io.zrz.graphql.client.runtime;

import io.zrz.graphql.core.doc.GQLDocument;
import io.zrz.graphql.core.value.GQLObjectValue;

/**
 * A channel between a GQL server and client.
 * 
 * The channel supports preparation of queries, although the backend protocol may not (e.g, stateless HTTP).
 * 
 * @author theo
 *
 */

public interface GQLChannel
{

  /**
   * Prepares the given document. This will return a handle which can then be used to execute the queries themselves on.
   */

  GQLPreparedStatement prepare(GQLDocument doc);

  /**
   * Executes without preparing.  Useful for one-shot execution. 
   */
  
  default GQLExecution execute(GQLDocument doc)
  {
    return prepare(doc).execute();
  }
  
  default GQLExecution execute(GQLDocument doc, GQLObjectValue input)
  {
    return prepare(doc).execute(input);
  }
  
  default GQLExecution execute(GQLDocument doc, String name, GQLObjectValue input)
  {
    return prepare(doc).execute(name, input);
  }
  
}
