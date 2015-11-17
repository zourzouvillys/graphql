package io.joss.graphql.core.binder;

import java.io.IOException;
import java.io.Writer;

import lombok.SneakyThrows;

/**
 * Note: we use lombok's @SneakyThrows to remove the typed check all the {@link IOException} exceptions and pass them onward. We would
 * otherwise anyway just catch and rethrow.
 * 
 * @author theo
 *
 */

public class DirectJsonGenerator implements BasicJsonGenerator
{

  private Writer writer;

  // set to true when the next item needs ot be a structual element or a comma.
  private boolean needComma = false;

  public DirectJsonGenerator(Writer writer)
  {
    this.writer = writer;
  }

  @SneakyThrows
  private void escape(String key)
  {
    if (key.indexOf('"') != -1 || key.indexOf('\\') != -1)
    {
      // slower path.
      for (int i = 0; i < key.length(); ++i)
      {
        char ch = key.charAt(i);
        switch (ch)
        {
          case '"':
            writer.write("\\\"");
            break;
          case '\\':
            writer.write("\\\\");
            break;
          default:
            writer.write(ch);
            break;
        }
      }
    }
    else
    {
      writer.write(key);
    }
  }

  @SneakyThrows
  public void flush()
  {
    writer.flush();
  }

  @SneakyThrows
  private void resetComma()
  {
    if (needComma)
    {
      writer.write(',');
    }
    needComma = false;
  }

  @SneakyThrows
  @Override
  public void writeStartObject()
  {
    resetComma();
    writer.write('{');
  }

  @SneakyThrows
  @Override
  public void writeFieldName(String key)
  {
    resetComma();
    writer.write('"');
    escape(key);
    writer.write("\":");
  }

  @SneakyThrows
  @Override
  public void writeEndObject()
  {
    writer.write('}');
    needComma = true;
  }

  @SneakyThrows
  @Override
  public void writeNull()
  {
    resetComma();
    writer.write("null");
    needComma = true;
  }

  @SneakyThrows
  @Override
  public void writeStartArray()
  {
    resetComma();
    writer.write('[');
  }

  @SneakyThrows
  @Override
  public void writeEndArray()
  {
    writer.write(']');
    needComma = true;
  }

  @SneakyThrows
  @Override
  public void writeBoolean(boolean b)
  {
    resetComma();
    writer.write(b ? "true" : "false");
    needComma = true;
  }

  @SneakyThrows
  @Override
  public void writeNumber(long value)
  {
    resetComma();
    writer.write(Long.toString(value));
    needComma = true;
  }

  @SneakyThrows
  @Override
  public void writeString(String value)
  {

    if (value == null)
    {
      writeNull();
      return;
    }

    resetComma();
    writer.write('"');
    this.escape(value);
    writer.write('"');
    needComma = true;
  }

  @SneakyThrows
  @Override
  public void writeNumber(double value)
  {
    resetComma();
    writer.write(Double.toString(value));
    needComma = true;
  }

}
