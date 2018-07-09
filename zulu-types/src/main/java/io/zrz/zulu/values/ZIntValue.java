package io.zrz.zulu.values;

import io.zrz.zulu.types.ZPrimitiveScalarType;
import io.zrz.zulu.types.ZScalarType;

public class ZIntValue implements ZScalarValue {

  private long value;

  public ZIntValue(long value) {
    this.value = value;
  }

  @Override
  public ZScalarType valueType() {
    return ZPrimitiveScalarType.INT;
  }

  @Override
  public String toString() {
    return Long.toString(value);
  }

  public long longValue() {
    return value;
  }

  public int intValue() {
    return (int) value;
  }

}
