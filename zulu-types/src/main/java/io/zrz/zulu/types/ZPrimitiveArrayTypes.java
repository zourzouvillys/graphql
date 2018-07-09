package io.zrz.zulu.types;

public enum ZPrimitiveArrayTypes implements ZArrayType {

  INT_ARRAY(ZPrimitiveScalarType.INT),

  STRING_ARRAY(ZPrimitiveScalarType.STRING),

  BOOLEAN_ARRAY(ZPrimitiveScalarType.BOOLEAN),

  DOUBLE_ARRAY(ZPrimitiveScalarType.DOUBLE)

  ;

  private ZTypeUse typeUse;

  ZPrimitiveArrayTypes(ZType componentType) {
    this.typeUse = new ZTypeUse(this);
  }

  @Override
  public ZTypeUse componentType() {
    return this.typeUse;
  }

}
