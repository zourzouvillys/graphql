package io.zrz.graphql.core.runtime;

import io.zrz.graphql.core.doc.GQLDocument;

/**
 * converts raw input documents to prepared ones which can be used to executing operations.
 * 
 * @author theo
 *
 */

public interface GQLDocumentManager {

  /**
   * prepares a document provided by a request.
   * 
   * depending on configuration, the implementation may cache the resulting document if the inputs and type system state
   * are the same.
   * 
   * @param input
   *          The raw input document, as a string.
   * 
   * @return A prepared document handle.
   * 
   */

  default GQLPreparedDocument prepareDocument(String input) {
    return prepareDocument(parse(input));
  }

  /**
   * prepare a parsed document.
   * 
   * @param doc
   * @return
   */

  GQLPreparedDocument prepareDocument(GQLDocument doc);

  /**
   * parse a document without preparing it.
   * 
   * @param input
   * @return
   */

  GQLDocument parse(String input);

}
