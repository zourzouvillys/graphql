package io.zrz.graphql.core.schema;

import io.zrz.graphql.core.schema.model.Model;

/**
 *
 * @author theo
 */

public interface SchemaProcessor {

  void process(Model tree);

}
