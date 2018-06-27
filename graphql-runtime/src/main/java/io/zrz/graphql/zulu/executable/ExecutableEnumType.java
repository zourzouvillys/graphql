package io.zrz.graphql.zulu.executable;

import com.google.common.reflect.TypeToken;

import io.zrz.graphql.zulu.LogicalTypeKind;
import io.zrz.graphql.zulu.executable.ExecutableSchemaBuilder.Symbol;
import io.zrz.zulu.types.ZTypeKind;

public class ExecutableEnumType implements ExecutableType {

  private final ExecutableSchema schema;
  private final String typeName;

  public ExecutableEnumType(final ExecutableSchema schema, final Symbol symbol, final BuildContext buildContext) {
    buildContext.add(symbol, this);
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

  @Override
  public TypeToken<?> javaType() {
    throw new IllegalStateException("no java type for enum");
  }

}
