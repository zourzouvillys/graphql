package io.zrz.zulu.values;

import io.zrz.zulu.types.ZPrimitiveScalarType;
import io.zrz.zulu.types.ZScalarType;

public class ZDoubleValue implements ZScalarValue {

  private double value;

  public ZDoubleValue(double value) {
    this.value = value;
  }

  @Override
  public ZScalarType valueType() {
    return ZPrimitiveScalarType.DOUBLE;
  }

  /**
   * only for debugging purposes!
   */

  @Override
  public String toString() {
    return Double.toString(value);
  }

}
