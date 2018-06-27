package io.zrz.graphql.zulu.doc;

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

  Optional<GQLPreparedOperation> defaultOperation();

  /**
   * returns the operation with the specified name. if the name is null, the default operation is returned if one
   * exists.
   */

  Optional<GQLPreparedOperation> operation(String name);

  /**
   * all operations in this document.
   */

  Stream<GQLPreparedOperation> operations();

  /**
   * the original parsed document.
   */

  GQLDocument document();

  /**
   * returns the operation with the specified name (if it exists), else returns the default operation (if exists).
   *
   * @param opname
   * @return
   */

  default Optional<GQLPreparedOperation> operation(final Optional<String> opname) {

    if (opname.isPresent()) {
      return operation(opname.get());
    }

    return this.defaultOperation();

  }

  void validate(GQLPreparedValidationListener listener);

}
