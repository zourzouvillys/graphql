package io.joss.graphql.core.binder;

/**
 * Avoid pulling in all of jackson/json lib just to serialize some strings
 * 
 * @author theo
 *
 */

public interface BasicJsonGenerator
{

  void writeStartObject();

  void writeFieldName(String key);

  void writeEndObject();

  void writeNull();

  void writeStartArray();

  void writeEndArray();

  //

  void writeBoolean(boolean b);

  void writeNumber(long value);

  void writeString(String value);

  void writeNumber(double value);

  void flush();

}
