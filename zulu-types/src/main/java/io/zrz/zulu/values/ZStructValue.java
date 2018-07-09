package io.zrz.zulu.values;

import java.util.Optional;

import io.zrz.zulu.types.ZStructType;

public interface ZStructValue extends ZValue {

  @Override
  ZStructType valueType();

  /**
   * provides the value of the specified field for this struct value instance.
   * 
   * @param name
   * @return
   */

  Optional<ZValue> fieldValue(String name);

}
