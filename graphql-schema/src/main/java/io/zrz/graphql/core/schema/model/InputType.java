package io.zrz.graphql.core.schema.model;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableMap;

import io.zrz.graphql.core.decl.GQLInputTypeDeclaration;
import io.zrz.graphql.core.schema.TypeVisitors;
import io.zrz.graphql.core.schema.TypeVisitors.NoReturnVisitor;

public class InputType extends AbstractType implements InputCompatibleType {

  private final List<String> descriptions;
  private final Map<String, InputField> fields;

  protected InputType(TypeBuilder typebuilder, Model model, String name, GQLInputTypeDeclaration decl, List<GQLInputTypeDeclaration> extensions) {

    super(typebuilder, model, name, decl, extensions);

    final List<String> descriptions = new LinkedList<>();

    if (decl.description() != null) {
      descriptions.add(decl.description());
    }

    final List<InputField> fields = new LinkedList<>();

    decl.fields().stream().map(d -> new InputField(typebuilder, this, this.model, d)).forEach(field -> {
      fields.add(field);
    });

    for (final GQLInputTypeDeclaration ext : extensions) {
      if (ext.description() != null) {
        descriptions.add(ext.description());
      }
      ext.fields().stream().map(d -> new InputField(typebuilder, this, this.model, d)).forEach(field -> {
        fields.add(field);
      });
    }

    this.descriptions = Collections.unmodifiableList(descriptions);
    this.fields = ImmutableMap.copyOf(fields.stream().collect(Collectors.toMap(in -> in.getName(), in -> in)));

  }

  @Override
  public <R> R apply(TypeVisitors.GenericReturnVisitor<R> visitor) {
    return visitor.visitInputType(this);
  }

  @Override
  public void apply(NoReturnVisitor visitor) {
    visitor.visitInputType(this);
  }

  public Collection<InputField> getFields() {
    return this.fields.values();
  }

  public InputField getField(String name) {
    return this.fields.get(name);
  }

}
