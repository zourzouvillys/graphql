package io.zrz.zulu.values;

import java.util.stream.Stream;

import io.zrz.zulu.types.ZArrayType;

public interface ZArrayValue extends ZValue {

  /**
   * the number of elements in this array of values.
   */

  int size();

  /**
   * fetch the value at the specified index.
   */

  ZValue get(int index);

  /**
   * stream of each of the values.
   */

  Stream<ZValue> values();

  /**
   * 
   */

  @Override
  ZArrayType valueType();

}
