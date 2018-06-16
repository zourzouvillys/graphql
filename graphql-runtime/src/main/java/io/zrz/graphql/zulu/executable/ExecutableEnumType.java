package io.zrz.graphql.zulu.executable;

import io.zrz.graphql.zulu.LogicalTypeKind;
import io.zrz.graphql.zulu.executable.ExecutableSchemaBuilder.Symbol;
import io.zrz.zulu.types.ZTypeKind;

public class ExecutableEnumType implements ExecutableType {

  private ExecutableSchema schema;
  private String typeName;

  public ExecutableEnumType(ExecutableSchema schema, Symbol symbol, BuildContext buildContext) {
    this.schema = schema;
    this.typeName = symbol.typeName;
  }

  @Override
  public ZTypeKind typeKind() {
    return ZTypeKind.ENUM;
  }

  @Override
  public LogicalTypeKind logicalKind() {
    return LogicalTypeKind.ENUM;
  }

  @Override
  public String typeName() {
    return this.typeName;
  }

}
