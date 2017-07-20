package io.joss.graphql.core.schema;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import com.google.common.collect.Lists;

import io.joss.graphql.core.decl.GQLDeclaration;
import io.joss.graphql.core.parser.GQLParser;
import io.joss.graphql.core.parser.GQLSourceInput;
import io.joss.graphql.core.schema.model.Model;
import lombok.SneakyThrows;

/**
 * Multiple schemas, with imports and type usage/sharing across files.
 * Calculates and resolves dependencies, and exposes a tree.
 */

public class SchemaCompiler {

  private final GQLParser parser = new GQLParser();
  private final List<InputUnit> inputs = Lists.newLinkedList();

  /**
   * compile and return the resolved schema
   */

  public static Model compile(Path... paths) {
    return new SchemaCompiler().add(paths).compile();
  }

  /**
   * compile and return the resolved schema
   */

  public static Model compile(String... strings) {
    return compile(GQLSourceInput.emptySource(), strings);
  }

  public static Model compile(GQLSourceInput source, String... strings) {
    return new SchemaCompiler().add(source, strings).compile();
  }

  /**
   * adds a source.
   *
   * @param path
   * @throws FileNotFoundException
   */

  public SchemaCompiler add(Path... paths) {
    Arrays.stream(paths).forEach(this::add);
    return this;
  }

  /**
   * adds a string source.
   *
   * @param path
   * @throws FileNotFoundException
   */

  public SchemaCompiler add(GQLSourceInput source, String... strings) {
    Arrays.stream(strings).forEach(e -> this.add(source, e));
    return this;
  }

  /**
   * adds a source.
   *
   * @param path
   * @throws FileNotFoundException
   */

  @SneakyThrows
  public SchemaCompiler add(Path path) {
    if (Files.isDirectory(path)) {
      Files.walk(path).filter(Files::isRegularFile).forEach(this::addFile);
    } else {
      this.addFile(path);
    }
    return this;
  }

  @SneakyThrows
  public SchemaCompiler addFile(Path path) {
    try {
      final List<GQLDeclaration> decls = this.parser.readSchema(new FileInputStream(path.toFile()), new GQLSourceInput(path.toFile().toString()));
      final InputUnit input = new InputUnit(path.toString(), decls);
      return this.add(input);
    } catch (final Exception ex) {
      throw new RuntimeException(String.format("While parsing %s (%s)", path.getFileName(), path), ex);
    }
  }

  private SchemaCompiler add(InputUnit input) {
    this.inputs.add(input);
    return this;
  }

  /**
   * adds a source.
   *
   * @param path
   * @throws FileNotFoundException
   */

  @SneakyThrows
  public SchemaCompiler add(GQLSourceInput source, String string) {
    final List<GQLDeclaration> decls = this.parser.readSchema(string, source);
    final InputUnit input = new InputUnit(source.getName(), decls);
    return this.add(input);
  }

  /**
   * performs resolution and construction of types, error checking, and semantic
   * validation.
   *
   * the result is a list of schemas and types, fully resolved.
   *
   */

  public Model compile() {

    // pass 1: merge types. for each type, generate a list of decls and
    // extensions.

    // must be no extensions without the declared type

    return Model.build(this.inputs);

  }
}
