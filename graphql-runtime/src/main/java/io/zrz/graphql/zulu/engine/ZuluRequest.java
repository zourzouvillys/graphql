package io.zrz.graphql.zulu.engine;

import io.zrz.graphql.zulu.executable.ExecutableTypeUse;

public class ZuluRequest {

  private ZuluParameterReader vars;

  public ZuluRequest(ZuluParameterReader variables) {
    this.vars = variables;
  }

  public Object parameter(String parameterName, ExecutableTypeUse targetType) {
    return vars.get(parameterName, targetType);
  }

}
