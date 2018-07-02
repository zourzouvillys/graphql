package io.zrz.graphql.zulu.api;

import io.zrz.graphql.zulu.LogicalTypeKind;
import io.zrz.zulu.types.ZStructType;
import io.zrz.zulu.types.ZType;

public final class ZuluSymbol {

  private final LogicalTypeKind typeKind;
  private final String typeName;
  private final ZType type;

  private ZuluSymbol(final LogicalTypeKind typeKind, final String typeName, final ZType type) {
    this.typeKind = typeKind;
    this.typeName = typeName;
    this.type = type;
  }

  public LogicalTypeKind typeKind() {
    return this.typeKind;
  }

  public String typeName() {
    return this.typeName;
  }

  public ZType type() {
    return this.type;
  }

  public static ZuluSymbol inputType(final String typeName, final ZStructType type) {
    return new ZuluSymbol(LogicalTypeKind.INPUT, typeName, type);
  }

}
