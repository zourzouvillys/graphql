package io.joss.graphql.core.schema;

import java.io.FileNotFoundException;
import java.nio.file.Paths;

import org.junit.Test;

import io.joss.graphql.core.schema.model.Model;

public class MultiSourceSchemaTest {

  @Test(expected = DuplicateDeclarationException.class)
  public void testMultiDecls() {
    SchemaCompiler.compile("input Hello { test: a }", " input Hello { test: b }");
  }

  @Test(expected = MissingTypeToExtendException.class)
  public void testExtension() {
    SchemaCompiler.compile("extend input Hello { test: a }", " extend input Hello { test: b }");
  }

  @Test(expected = DiffereringTypeException.class)
  public void testDifferingTypes1() {
    SchemaCompiler.compile("input Hello { test: a }", " extend type Hello { test: b }");
  }

  @Test(expected = DiffereringTypeException.class)
  public void testDifferingTypes2() {
    SchemaCompiler.compile("input Hello { test: a }", "type Hello { test: b }");
  }

  @Test(expected = DiffereringTypeException.class)
  public void testDifferingTypes3() {
    SchemaCompiler.compile("input Hello { test: a }", "extend input Hello { test: b }", "extend type Hello { test: b }");
  }

  @Test
  public void test1() throws FileNotFoundException {
    final Model res = SchemaCompiler.compile("input Hello {}", "extend input Hello {}");
    res.process(new MySchemaProcessor());
  }

  @Test
  public void testLarge() throws FileNotFoundException {
    final Model res = SchemaCompiler.compile(Paths.get("../../../workspaces/saasy/saasy/docs/schemas/"));
    res.process(new MySchemaProcessor());
  }

  private static final class MySchemaProcessor implements SchemaProcessor {

    @Override
    public void process(Model tree) {

      tree.exports().stream()
          .forEach(schema -> System.err.println(schema.query()));

    }

  }

}
