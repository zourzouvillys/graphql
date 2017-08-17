package io.joss.graphql.core.schema.model;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import io.joss.graphql.core.decl.GQLFieldDeclaration;
import io.joss.graphql.core.decl.GQLParameterableFieldDeclaration;
import io.joss.graphql.core.doc.GQLDirective;
import io.joss.graphql.core.utils.TypeRefStringGenerator;
import lombok.Getter;

@Getter
public class ObjectField {

  private final Model model;
  private final GQLFieldDeclaration decl;
  @Getter
  private final FieldContainerType objectType;
  @Getter
  private final String name;
  private final List<ObjectFieldParam> fieldArgs;
  @Getter
  private final TypeRef<Type> fieldType;

  public ObjectField(TypeBuilder builder, FieldContainerType object, Model model, GQLParameterableFieldDeclaration decl) {
    this.objectType = object;
    this.model = model;
    this.decl = decl;
    this.name = decl.name();
    this.fieldType = TypeRef.create(builder, decl.type());
    this.fieldArgs = decl.args().stream().map(arg -> new ObjectFieldParam(builder, arg)).collect(Collectors.toList());
  }

  @Override
  public String toString() {
    return String.format("%s: %s", this.decl.name(), this.decl.type().apply(TypeRefStringGenerator.getInstance()));
  }

  public Collection<ObjectFieldParam> getParameters() {
    return this.fieldArgs;
  }

  public Optional<GQLDirective> getDirective(String name) {
    return this.decl.directives().stream().filter(p -> p.name().equals(name)).findFirst();
  }

  public boolean hasDirective(String name) {
    return this.getDirective(name).isPresent();
  }

}
