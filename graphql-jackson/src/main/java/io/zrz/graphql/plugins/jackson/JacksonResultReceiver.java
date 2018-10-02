package io.zrz.graphql.plugins.jackson;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;

import io.zrz.graphql.zulu.engine.DefaultZuluResultReceiver;
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

  private final JsonGenerator jg;

  public JacksonResultReceiver(final JsonGenerator jg) {
    this.jg = jg;
  }

  @Override
  public void push(final ZuluSelectionContainer container, final Object instance) {
  }

  @Override
  public void pop(final ZuluSelectionContainer container, final Object instance) {
  }

  @Override
  public void startStruct(final ZuluSelectionContainer container, final Object context) {
    try {
      if (!this.jg.getOutputContext().inObject()) {
        this.jg.writeStartObject();
      }
      else {
        this.jg.writeObjectFieldStart(container.outputName());
      }
    }
    catch (

    final IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void endStruct(final ZuluSelectionContainer container, final Object context) {
    try {
      this.jg.writeEndObject();
    }
    catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void startList(final ZuluSelection container, final Object context) {
    try {
      if (this.jg.getOutputContext().inObject()) {
        this.jg.writeArrayFieldStart(container.outputName());
      }
      else {
        this.jg.writeStartArray();
      }
    }
    catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void endList(final ZuluSelection container, final Object context) {
    try {
      this.jg.writeEndArray();
    }
    catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void next(final Object instance) {
  }

  @Override
  public void write(final ZuluSelection field) {
    try {
      if (this.jg.getOutputContext().inObject()) {
        this.jg.writeNullField(field.outputName());
      }
      else {
        this.jg.writeNull();
      }
    }
    catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void write(final ZuluSelection field, final int value) {
    try {
      if (this.jg.getOutputContext().inObject()) {
        this.jg.writeNumberField(field.outputName(), value);
      }
      else {
        this.jg.writeNumber(value);
      }
    }
    catch (final IOException e) {
      throw new RuntimeException(e);
    }

  }

  @Override
  public void write(final ZuluSelection field, final long value) {
    try {
      if (this.jg.getOutputContext().inObject()) {
        this.jg.writeNumberField(field.outputName(), value);
      }
      else {
        this.jg.writeNumber(value);
      }
    }
    catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void write(final ZuluSelection field, final boolean value) {
    try {
      if (this.jg.getOutputContext().inObject()) {
        this.jg.writeBooleanField(field.outputName(), value);
      }
      else {
        this.jg.writeBoolean(value);
      }
    }
    catch (final IOException e) {
      throw new RuntimeException(e);
    }

  }

  @Override
  public void write(final ZuluSelection field, final double value) {
    try {
      if (this.jg.getOutputContext().inObject()) {
        this.jg.writeNumberField(field.outputName(), value);
      }
      else {
        this.jg.writeNumber(value);
      }
    }
    catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void write(final ZuluSelection field, final String value) {
    try {
      if (this.jg.getOutputContext().inObject()) {
        this.jg.writeStringField(field.outputName(), value);
      }
      else {
        this.jg.writeString(value);
      }
    }
    catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void write(final ZuluSelection field, final Object value) {
    try {
      if (this.jg.getOutputContext().inObject()) {
        this.jg.writeObjectField(field.outputName(), value);
      }
      else {
        this.jg.writeObject(value);
      }
    }
    catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  public JsonGenerator generator() {
    return this.jg;
  }

}
