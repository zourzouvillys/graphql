package io.zrz.graphql.plugins.jackson;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;

import io.zrz.graphql.zulu.engine.DefaultZuluResultReceiver;
import io.zrz.graphql.zulu.engine.ZuluExecutable;
import io.zrz.graphql.zulu.engine.ZuluResultReceiver;
import io.zrz.graphql.zulu.engine.ZuluSelection;
import io.zrz.graphql.zulu.engine.ZuluSelectionContainer;

/**
 * Zulu engine which outputs results using a jackson serializer.
 * 
 * JSON is the primary format, but ObjectMapper is used so YAML, XML, etc if you really wanted.
 * 
 * @author theo
 *
 */

public class JacksonResultReceiver extends DefaultZuluResultReceiver implements ZuluResultReceiver {

  private JsonGenerator jg;

  public JacksonResultReceiver(JsonGenerator jg) {
    this.jg = jg;
  }

  @Override
  public void push(ZuluSelectionContainer container, Object instance) {
    try {
      if (container instanceof ZuluExecutable) {
        jg.writeStartObject();
      }
      else if (container.isList()) {
        jg.writeArrayFieldStart(container.outputName());
      }
      else {
        jg.writeObjectFieldStart(container.outputName());
      }
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void pop(ZuluSelectionContainer container, Object instance) {
    try {
      if (jg.getOutputContext().inObject()) {
        jg.writeEndObject();
      }
      if (jg.getOutputContext().inArray()) {
        jg.writeEndArray();
      }
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void next(Object instance) {
    try {
      if (jg.getOutputContext().inObject()) {
        jg.writeEndObject();
      }
      jg.writeStartObject();
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void write(ZuluSelection field) {
    try {
      jg.writeNullField(field.outputName());
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void write(ZuluSelection field, int value) {
    try {
      jg.writeNumberField(field.outputName(), value);
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }

  }

  @Override
  public void write(ZuluSelection field, long value) {
    try {
      jg.writeNumberField(field.outputName(), value);
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void write(ZuluSelection field, boolean value) {
    try {
      jg.writeBooleanField(field.outputName(), value);
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }

  }

  @Override
  public void write(ZuluSelection field, double value) {
    try {
      jg.writeNumberField(field.outputName(), value);
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void write(ZuluSelection field, String value) {
    try {
      jg.writeStringField(field.outputName(), value);
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void write(ZuluSelection field, Object value) {
    try {
      jg.writeObjectField(field.outputName(), value);
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public JsonGenerator generator() {
    return this.jg;
  }

}
