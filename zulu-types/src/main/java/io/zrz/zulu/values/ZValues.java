package io.zrz.zulu.values;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import io.zrz.zulu.types.ZArrayType;
import io.zrz.zulu.types.ZPrimitiveArrayTypes;

public final class ZValues {

  private ZValues() {
    // no-instantiate
  }

  public static final ZBoolValue of(boolean value) {
    return new ZBoolValue(value);
  }

  public static final ZIntValue of(long value) {
    return new ZIntValue(value);
  }

  public static final ZIntValue toInt(long value) {
    return new ZIntValue(value);
  }

  public static final ZIntValue toInt(int value) {
    return new ZIntValue(value);
  }

  public final static ZStringValue of(String value) {
    return new ZStringValue(value);
  }

  public final static ZDoubleValue of(double value) {
    return new ZDoubleValue(value);
  }

  public final static ZStringValue toString(long value) {
    return new ZStringValue(Long.toString(value));
  }

  public static ZArrayValue ofList(int... values) {

    return new ZArrayValue() {

      @Override
      public Stream<ZValue> values() {
        return IntStream.of(values).mapToObj(x -> ZValues.of(x));
      }

      @Override
      public ZArrayType valueType() {
        return ZPrimitiveArrayTypes.INT_ARRAY;
      }

      @Override
      public int size() {
        throw new IllegalArgumentException();
      }

      @Override
      public ZValue get(int index) {
        throw new IllegalArgumentException();
      }

    };

  }

}
