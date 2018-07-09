package io.zrz.zulu.values;

import io.zrz.zulu.types.ZPrimitiveScalarType;
import io.zrz.zulu.types.ZScalarType;

public class ZStringValue implements ZScalarValue {

  private String value;

  public ZStringValue(String value) {
    this.value = value;
  }

  @Override
  public ZScalarType valueType() {
    return ZPrimitiveScalarType.STRING;
  }

  /**
   * only for debugging purposes!
   */

  @Override
  public String toString() {
    return "'" + value.replace("'", "\\'") + "'";
  }

  public String stringValue() {
    return this.value;
  }

}
