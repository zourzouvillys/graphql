package io.joss.graphql.core.schema.model;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import io.joss.graphql.core.decl.GQLInterfaceTypeDeclaration;
import io.joss.graphql.core.schema.TypeVisitors;
import io.joss.graphql.core.schema.TypeVisitors.NoReturnVisitor;
import lombok.Getter;

public class InterfaceType extends AbstractType implements FieldContainerType {

  @Getter
  private final List<String> descriptions;
  @Getter
  private final List<ObjectField> fields;

  InterfaceType(TypeBuilder typebuilder, Model model, String name, GQLInterfaceTypeDeclaration decl, Collection<GQLInterfaceTypeDeclaration> extensions) {
    super(typebuilder, model, name, decl, extensions);

    final List<String> descriptions = new LinkedList<>();

    if (decl.description() != null) {
      descriptions.add(decl.description());
    }

    final List<ObjectField> fields = new LinkedList<>();

    decl.fields().stream().map(d -> new ObjectField(typebuilder, this, this.model, d)).forEach(field -> {
      fields.add(field);
    });

    for (final GQLInterfaceTypeDeclaration ext : extensions) {
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
    return visitor.visitInterfaceType(this);
  }

  @Override
  public void apply(NoReturnVisitor visitor) {
    visitor.visitInterfaceType(this);
  }

}
