package io.zrz.graphql.jersey;


import lombok.Builder;
import lombok.ToString;

@Builder(builderClassName = "Builder")
@ToString
public final class MutationHandler
{

  private String name;
  private Class<?> returnType;
  private Class<?> inputType;

  /**
   * The name of this mutation.
   */

  public String name()
  {
    return this.name;
  }

  /**
   * The expected input type for this mutation.
   */

  public Class<?> inputType()
  {
    return this.inputType;
  }

  public Class<?> returnType()
  {
    return returnType;
  }

}