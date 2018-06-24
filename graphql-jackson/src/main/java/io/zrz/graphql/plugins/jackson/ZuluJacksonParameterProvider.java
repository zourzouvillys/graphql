package io.zrz.graphql.plugins.jackson;

import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import io.zrz.graphql.zulu.engine.ZuluParameterReader;
import io.zrz.graphql.zulu.executable.ExecutableTypeUse;

public class ZuluJacksonParameterProvider implements ZuluParameterReader {

  private Map<String, JsonNode> vars;

  public ZuluJacksonParameterProvider(Map<String, JsonNode> variables) {
    this.vars = variables;
  }

  @Override
  public Object get(String parameterName, ExecutableTypeUse targetType) {
    throw new IllegalArgumentException("todo");
  }

}
