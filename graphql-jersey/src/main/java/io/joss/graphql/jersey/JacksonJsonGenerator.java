package io.joss.graphql.jersey;

import io.joss.graphql.core.binder.BasicJsonGenerator;


import java.io.Writer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.joss.graphql.core.binder.BasicJsonGenerator;
import lombok.SneakyThrows;

public class JacksonJsonGenerator implements BasicJsonGenerator
{

  private JsonGenerator jg;

  @SneakyThrows
  public JacksonJsonGenerator(ObjectMapper mapper, Writer writer, boolean prettyPrint)
  {
    this.jg = mapper.getFactory().createGenerator(writer);
    if (prettyPrint)
    {
      DefaultPrettyPrinter pp = new DefaultPrettyPrinter().withoutSpacesInObjectEntries();
      jg.setPrettyPrinter(pp);
    }
  }

  @SneakyThrows
  @Override
  public void writeStartObject()
  {
    jg.writeStartObject();
  }

  @SneakyThrows
  @Override
  public void writeFieldName(String key)
  {
    jg.writeFieldName(key);
  }

  @SneakyThrows
  @Override
  public void writeEndObject()
  {
    jg.writeEndObject();

  }

  @SneakyThrows
  @Override
  public void writeNull()
  {
    jg.writeNull();
  }

  @SneakyThrows
  @Override
  public void writeStartArray()
  {
    jg.writeStartArray();

  }

  @SneakyThrows
  @Override
  public void writeEndArray()
  {
    jg.writeEndArray();

  }

  @SneakyThrows
  @Override
  public void writeBoolean(boolean b)
  {
    jg.writeBoolean(b);
  }

  @SneakyThrows
  @Override
  public void writeNumber(long value)
  {
    jg.writeNumber(value);
  }

  @SneakyThrows
  @Override
  public void writeString(String value)
  {
    jg.writeString(value);
  }

  @SneakyThrows
  @Override
  public void writeNumber(double value)
  {
    jg.writeNumber(value);
  }

  @SneakyThrows
  @Override
  public void flush()
  {
    jg.flush();
  }

}