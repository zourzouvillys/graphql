package io.zrz.graphql.zulu;

import java.util.Map;
import java.util.Optional;

import io.zrz.zulu.types.ZStructType;

/**
 * an output type, which is essentially a struct with each field having an optional inputType for the parameters it
 * accepts.
 * 
 * @author theo
 *
 */

public interface ZOutputType extends ZStructType {

  /**
   * each field in this output type.
   */

  @Override
  Map<String, ? extends ZOutputField> fields();

  @Override
  default Optional<? extends ZOutputField> field(String name) {
    return Optional.ofNullable(this.fields().get(name));
  }

}
