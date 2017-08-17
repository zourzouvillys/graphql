package io.joss.graphql.core.schema.model;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import io.joss.graphql.core.decl.GQLObjectTypeDeclaration;
import io.joss.graphql.core.schema.TypeVisitors;
import io.joss.graphql.core.schema.TypeVisitors.NoReturnVisitor;

public class ObjectType extends AbstractType implements FieldContainerType {

  private final List<String> descriptions;
  private final List<ObjectField> fields;

  ObjectType(TypeBuilder typebuilder, Model model, String name, GQLObjectTypeDeclaration decl, List<GQLObjectTypeDeclaration> extensions) {

    super(typebuilder, model, name, decl, extensions);

    final List<String> descriptions = new LinkedList<>();

    if (decl.description() != null) {
      descriptions.add(decl.description());
    }

    final List<ObjectField> fields = new LinkedList<>();

    decl.fields().stream().map(d -> new ObjectField(typebuilder, this, this.model, d)).forEach(field -> {
      fields.add(field);
    });

    for (final GQLObjectTypeDeclaration ext : extensions) {
      if (ext.description() != null) {
        descriptions.add(ext.description());
      }
      ext.fields().stream().map(d -> new ObjectField(typebuilder, this, this.model, d)).forEach(field -> {
        fields.add(field);
      });
    }

    this.descriptions = Collections.unmodifiableList(descriptions);
    this.fields = Collections.unmodifiableList(fields);

  }

  @Override
  public <R> R apply(TypeVisitors.GenericReturnVisitor<R> visitor) {
    return visitor.visitObjectType(this);
  }

  @Override
  public void apply(NoReturnVisitor visitor) {
    visitor.visitObjectType(this);
  }

  public List<ObjectField> getFields() {
    return this.fields;
  }

}
