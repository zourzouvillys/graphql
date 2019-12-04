package io.zrz.graphql.zulu.executable;

import com.google.common.reflect.TypeToken;

import io.zrz.graphql.zulu.LogicalTypeKind;
import io.zrz.graphql.zulu.executable.ExecutableSchemaBuilder.Symbol;
import io.zrz.zulu.types.ZTypeKind;

public class ExecutableScalarType implements ExecutableType {

  private final ExecutableSchema schema;
  private final String typeName;
  private final TypeToken<?> javaType;

  public ExecutableScalarType(final ExecutableSchema schema, final Symbol symbol, final BuildContext buildContext) {
    buildContext.add(symbol, this);
    this.schema = schema;
    this.typeName = symbol.typeName;
    this.javaType = symbol.typeToken;
  }

  @Override
  public ZTypeKind typeKind() {
    return ZTypeKind.SCALAR;
  }

  @Override
  public LogicalTypeKind logicalKind() {
    return LogicalTypeKind.SCALAR;
  }

  @Override
  public String typeName() {
    return this.typeName;
  }

  @Override
  public TypeToken<?> javaType() {
    return this.javaType;
  }

  @Override
  public String toString() {
    return "scalar type " + this.typeName;
  }
  
}
