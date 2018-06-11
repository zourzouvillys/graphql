package io.zrz.graphql.core.runtime;

import java.util.Optional;
import java.util.stream.Stream;

import io.zrz.graphql.core.doc.GQLDocument;

/**
 * a prepared document, which is the result of parsing it and ensuring the types are valid. execution after preparing a
 * document is faster that parsing/analyzing each time.
 * 
 * all documents prepared in an instance will use the same runtime type system, so if you expose multiple type models to
 * different users ensure you prepare documents separately based on the types available to them.
 * 
 * @author theo
 *
 */

public interface GQLPreparedDocument {

  /**
   * the default operation (always a query) in this document if it has one.
   */

  Optional<? extends GQLPreparedOperation> defaultOperation();

  /**
   * returns the operation with the specified name.
   */

  Optional<? extends GQLPreparedOperation> operation(String name);

  /**
   * all operations in this document.
   */

  Stream<? extends GQLPreparedOperation> operations();

  /**
   * the original parsed document.
   */

  GQLDocument document();

}
