package io.joss.graphql.core.schema.model;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;

import io.joss.graphql.core.schema.InputUnit;
import io.joss.graphql.core.schema.SchemaProcessor;
import io.joss.graphql.core.types.GQLDeclarationRef;
import lombok.Getter;

/**
 * a fully resolved schema.
 *
 * @author theo
 *
 */

public class Model {

  @Getter
  private final Map<String, ? extends Type> types;

  @Getter
  private final Set<Schema> schemas;

  public Model(List<InputUnit> inputs) {

    final TypeBuilder b = new TypeBuilder(this, inputs);

    this.types = b.build();

    this.schemas = inputs.stream()
        .flatMap(in -> in.schemas().stream())
        .map(Schema::new)
        .collect(Collectors.toSet());

    // final NoObjectsInInputCheck checks = new NoObjectsInInputCheck();
    // this.types.forEach(type -> type.apply(checks));

  }

  public void process(SchemaProcessor processor) {
    processor.process(this);
  }

  public static Model build(List<InputUnit> inputs) {
    return new Model(inputs);
  }

  public Collection<InputType> getInputTypes() {
    return this.types.entrySet().stream().filter(type -> type.getValue().getClass().isAssignableFrom(InputType.class))
        .map(in -> InputType.class.cast(in.getValue()))
        .collect(Collectors.toList());
  }

  public Type getType(String name) {
    return this.types.get(name);
  }

  public Type getType(GQLDeclarationRef value) {
    return this.getType(value.name());
  }

  public ObjectType getObjectType(GQLDeclarationRef value) {
    final Type lookup = this.types.get(value.name());
    Preconditions.checkNotNull(lookup, value.name());
    return (ObjectType) lookup;
  }

  public InputType getInputType(GQLDeclarationRef value) {
    return (InputType) this.types.get(value.name());
  }

}
