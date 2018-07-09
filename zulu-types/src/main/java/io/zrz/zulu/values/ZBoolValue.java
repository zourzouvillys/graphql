package io.zrz.zulu.values;

import io.zrz.zulu.types.ZPrimitiveScalarType;
import io.zrz.zulu.types.ZScalarType;

public class ZBoolValue implements ZScalarValue {

  private boolean value;

  public ZBoolValue(boolean value) {
    this.value = value;
  }

  @Override
  public ZScalarType valueType() {
    return ZPrimitiveScalarType.BOOLEAN;
  }

  @Override
  public String toString() {
    return Boolean.toString(value);
  }

  public boolean boolValue() {
    return value;
  }

}
