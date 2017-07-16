package io.joss.graphql.core.schema.model;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.joss.graphql.core.decl.GQLTypeDeclaration;
import io.joss.graphql.core.schema.InputUnit;
import io.joss.graphql.core.schema.SchemaProcessor;
import io.joss.graphql.core.schema.TypeUtils;

/**
 * a fully resolved schema.
 *
 * @author theo
 *
 */

public class Model {

  private final Set<Type> types;
  private final Set<Schema> schemas;

  public Model(Set<Type> types, Set<Schema> schemas) {
    this.types = types;
    this.schemas = schemas;
  }

  public void process(SchemaProcessor processor) {
    processor.process(this);
  }

  /**
   * The exposed schemas.
   *
   * deviation from standard GraphQL schema IDL: a schema may have a name.
   *
   */

  public Set<Schema> exports() {
    return this.schemas;
  }

  public static Model build(List<InputUnit> inputs) {

    final Set<Type> types = declaredSymbols(inputs).entrySet().stream()
        .map(e -> TypeUtils.build(e.getKey(), e.getValue()))
        .collect(Collectors.toSet());

    final Set<Schema> schemas = inputs.stream()
        .flatMap(in -> in.schemas().stream())
        .map(Schema::new)
        .collect(Collectors.toSet());

    return new Model(types, schemas);

  }

  /**
   * complete list of declared symbols (including extensions). not checked for
   * duplicates.
   */

  private static Map<String, Set<GQLTypeDeclaration>> declaredSymbols(List<InputUnit> inputs) {
    return inputs.stream()
        .flatMap(in -> in.types().entrySet().stream())
        .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue(), Model::merge));
  }

  /**
   * merge when we have a conflict.
   *
   * @param a
   * @param b
   * @return
   */

  private static Set<GQLTypeDeclaration> merge(Set<GQLTypeDeclaration> a, Set<GQLTypeDeclaration> b) {
    return Stream.concat(a.stream(), b.stream())
        .collect(Collectors.toSet());
  }

}
