package io.joss.graphql.core.schema;

import io.joss.graphql.core.schema.model.Model;

/**
 *
 * @author theo
 */

public interface SchemaProcessor {

  void process(Model tree);

}
