package io.zrz.graphql.core.binder.execution;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

import org.junit.Before;
import org.junit.Test;

import io.zrz.graphql.core.binder.DirectJsonGenerator;
import lombok.SneakyThrows;

public class DirectJsonGeneratorTest
{

  private OutputStreamWriter strm;
  private DirectJsonGenerator gen;
  private ByteArrayOutputStream buffer;

  @Before
  public void seutp()
  {
    this.buffer = new ByteArrayOutputStream();
    this.strm = new OutputStreamWriter(buffer);
    this.gen = new DirectJsonGenerator(strm);

  }

  @Test
  public void testEmptyArray() throws IOException
  {
    gen.writeStartArray();
    gen.writeEndArray();
    assertEquals("[]", buffer());
  }

  @Test
  public void testEmptyObject() throws IOException
  {
    gen.writeStartObject();
    gen.writeEndObject();
    assertEquals("{}", buffer());
  }

  @Test
  public void testSingleValueObject() throws IOException
  {
    gen.writeStartObject();
    gen.writeFieldName("a");
    gen.writeNumber(1);
    gen.writeEndObject();
    assertEquals("{\"a\":1}", buffer());
  }

  @Test
  public void testMuliValueObject() throws IOException
  {
    gen.writeStartObject();
    gen.writeFieldName("a");
    gen.writeNumber(1);
    gen.writeFieldName("b");
    gen.writeNumber(2);
    gen.writeEndObject();
    assertEquals("{\"a\":1,\"b\":2}", buffer());
  }

  @Test
  public void testNullValueObject() throws IOException
  {
    gen.writeStartObject();
    gen.writeFieldName("a");
    gen.writeNull();
    gen.writeFieldName("b");
    gen.writeNumber(2);
    gen.writeFieldName("c");
    gen.writeString("hello");
    gen.writeEndObject();
    assertEquals("{\"a\":null,\"b\":2,\"c\":\"hello\"}", buffer());
  }

  @Test
  public void testString() throws IOException
  {
    gen.writeString("abc");
    assertEquals("\"abc\"", buffer());
  }

  @Test
  public void testEscapeString1() throws IOException
  {
    gen.writeString("xxa\"x");
    assertEquals("\"xxa\\\"x\"", buffer());
  }

  @Test
  public void testEscapeString2() throws IOException
  {
    gen.writeString("xxa\"");
    assertEquals("\"xxa\\\"\"", buffer());
  }

  @SneakyThrows
  private String buffer()
  {
    gen.flush();
    strm.flush();
    return new String(buffer.toByteArray(), StandardCharsets.UTF_8);
  }

}
