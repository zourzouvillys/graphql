package io.zrz.graphql.zulu;

import java.util.Optional;

import io.zrz.zulu.types.ZField;
import io.zrz.zulu.types.ZStructType;

public interface ZOutputField extends ZField {

  /**
   * each output field can have a set of parameters, defined as a structure.
   */

  Optional<? extends ZStructType> parameters();

}
