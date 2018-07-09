package io.zrz.zulu.values;

import io.zrz.zulu.types.ZScalarType;

/**
 * 
 */

public interface ZScalarValue extends ZValue {

  @Override
  ZScalarType valueType();

}
