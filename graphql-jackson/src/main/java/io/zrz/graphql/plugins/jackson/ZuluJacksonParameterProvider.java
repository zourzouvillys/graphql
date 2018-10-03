package io.zrz.graphql.plugins.jackson;

import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Streams;

import io.zrz.graphql.zulu.engine.ZuluParameterReader;
import io.zrz.graphql.zulu.executable.ExecutableInput;

public class ZuluJacksonParameterProvider implements ZuluParameterReader {

  private final Map<String, JsonNode> vars;
  private final ObjectMapper mapper;

  public ZuluJacksonParameterProvider(final ObjectMapper mapper, final Map<String, JsonNode> variables) {
    this.mapper = mapper;
    this.vars = variables == null ? ImmutableMap.of() : variables;
  }

  public ZuluJacksonParameterProvider(final ObjectMapper mapper, final ObjectNode variables) {
    this.mapper = mapper;
    this.vars = variables != null
        ? Streams.stream(variables.fields()).collect(ImmutableMap.toImmutableMap(e -> e.getKey(), e -> e.getValue()))
        : ImmutableMap.of();
  }

  @Override
  public Object get(final String parameterName, final ExecutableInput targetType) {

    final JsonNode value = this.vars.get(parameterName);

    if (value == null) {
      return null;
    }

    return this.mapper.convertValue(
        value,
        this.mapper.getTypeFactory().constructType(targetType.javaGenericType()));

  }

  @Override
  public boolean has(final String parameterName) {
    if (this.vars == null) {
      return false;
    }
    return this.vars.containsKey(parameterName);
  }

}
