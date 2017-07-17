package io.joss.graphql.core.schema.model;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import io.joss.graphql.core.decl.GQLInputTypeDeclaration;
import io.joss.graphql.core.schema.TypeVisitors;
import io.joss.graphql.core.schema.TypeVisitors.NoReturnVisitor;

public class InputType extends AbstractType {

  private final List<String> descriptions;
  private final List<InputField> fields;

  protected InputType(TypeBuilder typebuilder, Model model, String name, GQLInputTypeDeclaration decl, List<GQLInputTypeDeclaration> extensions) {

    super(typebuilder, model, name);

    final List<String> descriptions = new LinkedList<>();

    if (decl.description() != null) {
      descriptions.add(decl.description());
    }

    final List<InputField> fields = new LinkedList<>();

    decl.fields().stream().map(d -> new InputField(this, this.model, d)).forEach(field -> {
      fields.add(field);
    });

    for (final GQLInputTypeDeclaration ext : extensions) {
      if (ext.description() != null) {
        descriptions.add(ext.description());
      }
      ext.fields().stream().map(d -> new InputField(this, this.model, d)).forEach(field -> {
        fields.add(field);
      });
    }

    this.descriptions = Collections.unmodifiableList(descriptions);
    this.fields = Collections.unmodifiableList(fields);

  }

  @Override
  public <R> R apply(TypeVisitors.GenericReturnVisitor<R> visitor) {
    return visitor.visitInputType(this);
  }

  @Override
  public void apply(NoReturnVisitor visitor) {
    visitor.visitInputType(this);
  }

  public List<InputField> getFields() {
    return this.fields;
  }

}
