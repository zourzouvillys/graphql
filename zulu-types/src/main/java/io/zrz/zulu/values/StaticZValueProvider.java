package io.zrz.zulu.values;

import java.util.Objects;

import io.zrz.zulu.types.ZTypeUse;

public class StaticZValueProvider implements ZValueProvider {

  private ZValue value;

  StaticZValueProvider(ZValue value) {
    this.value = Objects.requireNonNull(value);
  }

  public static ZValueProvider of(long value) {
    return new StaticZValueProvider(ZValues.of(value));
  }

  public static ZValueProvider of(String value) {
    return new StaticZValueProvider(ZValues.of(value));
  }

  public static ZValueProvider of(boolean value) {
    return new StaticZValueProvider(ZValues.of(value));
  }

  @Override
  public ZValue resolve() {
    return this.value;
  }

  @Override
  public ZTypeUse type() {
    return ZTypeUse.of(this.value.valueType());
  }

  @Override
  public String toString() {
    return this.value.toString();
  }

}
