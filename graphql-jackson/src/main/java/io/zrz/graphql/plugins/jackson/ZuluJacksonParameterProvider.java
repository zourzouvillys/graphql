package io.zrz.graphql.plugins.jackson;

import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

import io.zrz.graphql.zulu.engine.ZuluParameterReader;
import io.zrz.graphql.zulu.executable.ExecutableInputField;

public class ZuluJacksonParameterProvider implements ZuluParameterReader {

  private final static ObjectMapper mapper = new ObjectMapper()
      .registerModule(new ParameterNamesModule())
      .registerModule(new Jdk8Module())
      .registerModule(new JavaTimeModule());

  private final Map<String, JsonNode> vars;

  public ZuluJacksonParameterProvider(final Map<String, JsonNode> variables) {
    this.vars = variables;
  }

  @Override
  public Object get(final String parameterName, final ExecutableInputField targetType) {

    final JsonNode value = this.vars.get(parameterName);

    if (value == null) {
      return null;
    }

    return mapper.convertValue(value, mapper.getTypeFactory().constructType(targetType.javaType().getType()));

  }

  @Override
  public boolean has(final String parameterName) {
    if (this.vars == null)
      return false;
    return this.vars.containsKey(parameterName);
  }

}
