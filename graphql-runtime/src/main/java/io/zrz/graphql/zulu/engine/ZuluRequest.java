package io.zrz.graphql.zulu.engine;

import io.zrz.graphql.zulu.executable.ExecutableInput;

public class ZuluRequest {

  private final ZuluParameterReader vars;

  public ZuluRequest(final ZuluParameterReader variables) {
    this.vars = variables;
  }

  public Object parameter(final String parameterName, final ExecutableInput targetType) {
    return this.vars.get(parameterName, targetType);
  }

  public boolean hasVariable(final String key) {
    return this.vars.has(key);
  }

}
