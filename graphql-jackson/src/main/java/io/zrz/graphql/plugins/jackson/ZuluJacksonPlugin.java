package io.zrz.graphql.plugins.jackson;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

import io.zrz.graphql.zulu.engine.ZuluEngineBuilder;
import io.zrz.graphql.zulu.plugins.ZuluPlugin;

public class ZuluJacksonPlugin implements ZuluPlugin {

  private final ObjectMapper mapper;

  public ZuluJacksonPlugin(final Module... modules) {
    this.mapper = new ObjectMapper()
        .registerModule(new ParameterNamesModule())
        .registerModule(new Jdk8Module())
        .registerModule(new JavaTimeModule())
        .registerModules(modules);
  }

  @Override
  public void onPluginRegistered(final ZuluEngineBuilder builder) {
    builder.schema(schema -> schema.typeBinder(new ZuluJacksonTypeBinder(this.mapper)));
  }

}
