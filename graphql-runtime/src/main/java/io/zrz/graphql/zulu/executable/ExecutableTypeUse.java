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

  private String typeName;
  private TypeToken<?> javaType;
  private final int arity;
  private Symbol symbol;
  private final ExecutableType type;

  public ExecutableTypeUse(TypeToken<?> javaType, ExecutableType type, int arity, Symbol symbol) {
    super(type);
    this.javaType = javaType;
    this.typeName = type.typeName();
    this.type = Objects.requireNonNull(type);
    this.arity = arity;
    this.symbol = symbol;
  }

  public ExecutableTypeUse(TypeToken<?> javaType, String typeName, int arity, Symbol symbol, ExecutableType decl) {
    super(decl);
    this.javaType = javaType;
    this.typeName = typeName;
    this.arity = arity;
    this.symbol = symbol;
    this.type = Objects.requireNonNull(decl, javaType.toString());
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
    return symbol.typeKind;
  }

  /**
   * the java type.
   */

  public TypeToken<?> javaType() {
    return javaType;
  }

  /**
   * the GraphQL type name.
   */

  public String logicalType() {
    return typeName;
  }

  @Override
  public String toString() {
    return this.javaType + " -> " + this.typeName;
  }

}
