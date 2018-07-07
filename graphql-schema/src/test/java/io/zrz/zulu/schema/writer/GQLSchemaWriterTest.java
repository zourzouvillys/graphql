package io.zrz.zulu.schema.writer;

import org.junit.Test;

import io.zrz.graphql.core.types.GQLTypeDeclKind;

public class GQLSchemaWriterTest {

  @Test
  public void test() {

    final GQLSchemaWriter w = new GQLSchemaWriter(System.out);

    w.startType(GQLTypeDeclKind.INTERFACE, "HelloConfig");

    w.writeNullableField("template", "String");
    w.writeStartDirective("thing");
    w.writeFieldName("title");
    w.writeStringValue("Hello World");
    w.writeEndField();

    w.writeRequiredField("translator", "Another");
    w.writeStartDirective("something");
    w.writeFieldName("type");
    w.writeStringValue("my.endpoint.v1");
    w.writeEndDirective();

    w.writeStartDirective("description");
    w.writeFieldName("title");
    w.writeStringValue("Hello More\"?\"");
    w.writeEndField();

    w.writeEndType();

  }

}
