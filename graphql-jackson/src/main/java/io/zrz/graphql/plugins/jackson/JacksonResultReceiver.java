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

  private final JsonGenerator jg;

  public JacksonResultReceiver(final JsonGenerator jg) {
    this.jg = jg;
  }

  @Override
  public void push(final ZuluSelectionContainer container, final Object instance) {
    try {
      if (container instanceof ZuluExecutable) {
        this.jg.writeStartObject();
      }
      else if (container.isList()) {
        this.jg.writeArrayFieldStart(container.outputName());
      }
      else {
        this.jg.writeObjectFieldStart(container.outputName());
      }
    }
    catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void pop(final ZuluSelectionContainer container, final Object instance) {
    try {
      if (this.jg.getOutputContext().inObject()) {
        this.jg.writeEndObject();
      }
      if (this.jg.getOutputContext().inArray()) {
        this.jg.writeEndArray();
      }
    }
    catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void next(final Object instance) {
    try {
      if (this.jg.getOutputContext().inObject()) {
        this.jg.writeEndObject();
      }
      this.jg.writeStartObject();
    }
    catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void write(final ZuluSelection field) {
    // try {
    // this.jg.writeNullField(field.outputName());
    // }
    // catch (final IOException e) {
    // throw new RuntimeException(e);
    // }
  }

  @Override
  public void write(final ZuluSelection field, final int value) {
    try {
      this.jg.writeNumberField(field.outputName(), value);
    }
    catch (final IOException e) {
      throw new RuntimeException(e);
    }

  }

  @Override
  public void write(final ZuluSelection field, final long value) {
    try {
      this.jg.writeNumberField(field.outputName(), value);
    }
    catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void write(final ZuluSelection field, final boolean value) {
    try {
      this.jg.writeBooleanField(field.outputName(), value);
    }
    catch (final IOException e) {
      throw new RuntimeException(e);
    }

  }

  @Override
  public void write(final ZuluSelection field, final double value) {
    try {
      this.jg.writeNumberField(field.outputName(), value);
    }
    catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void write(final ZuluSelection field, final String value) {
    try {
      this.jg.writeStringField(field.outputName(), value);
    }
    catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void write(final ZuluSelection field, final Object value) {
    try {
      this.jg.writeObjectField(field.outputName(), value);
    }
    catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  public JsonGenerator generator() {
    return this.jg;
  }

}
