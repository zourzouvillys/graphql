package io.zrz.graphql.zulu.executable;

import java.util.Objects;

import com.google.common.reflect.TypeToken;

import io.zrz.graphql.zulu.LogicalTypeKind;
import io.zrz.graphql.zulu.executable.ExecutableSchemaBuilder.Symbol;
import io.zrz.zulu.types.ZTypeUse;

/**
 * both input and output types have a java type as well as a GraphQL type that represents them.
 *
 * this includes both.
 *
 * @author theo
 *
 */

public class ExecutableTypeUse extends ZTypeUse {

  private final String typeName;
  private final TypeToken<?> javaType;
  private final int arity;
  private final Symbol symbol;
  private final ExecutableType type;
  private final boolean nullable;

  public ExecutableTypeUse(final TypeToken<?> javaType, final ExecutableType type, final int arity, final Symbol symbol, final boolean nullable) {
    super(type);
    this.javaType = javaType;
    this.typeName = type.typeName();
    this.type = Objects.requireNonNull(type);
    this.arity = arity;
    this.symbol = symbol;
    this.nullable = nullable;
  }

  public ExecutableTypeUse(
      final TypeToken<?> javaType,
      final String typeName,
      final int arity,
      final Symbol symbol,
      final ExecutableType decl,
      final boolean nullable) {
    super(decl);
    this.javaType = javaType;
    this.typeName = typeName;
    this.arity = arity;
    this.symbol = symbol;
    this.type = Objects.requireNonNull(decl, javaType.toString());
    this.nullable = nullable;
  }

  @Override
  public ExecutableType type() {
    return this.type;
  }

  public int arity() {
    return this.arity;
  }

  /**
   * the logical type kind for this type (e.g, the GraphQL type).
   */

  public LogicalTypeKind logicalTypeKind() {
    return this.symbol.typeKind;
  }

  /**
   * the java type.
   */

  public TypeToken<?> javaType() {
    return this.javaType;
  }

  /**
   * the GraphQL type name.
   */

  public String logicalType() {
    return this.typeName;
  }

  @Override
  public String toString() {
    return this.javaType + " -> " + this.typeName + (this.nullable ? "" : "!");
  }

  public boolean isNullable() {
    return this.nullable;
  }

}
