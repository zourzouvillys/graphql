package io.zrz.graphql.zulu.executable;

import com.google.common.reflect.TypeToken;

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

  public ExecutableTypeUse(TypeToken<?> javaType, ExecutableType type) {
    super(type);
    this.javaType = javaType;
    this.typeName = type.typeName();
  }

  public ExecutableTypeUse(TypeToken<?> javaType, String typeName) {
    super(null);
    this.javaType = javaType;
    this.typeName = typeName;
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

}
