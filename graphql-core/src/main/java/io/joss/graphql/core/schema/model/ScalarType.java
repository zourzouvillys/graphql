package io.joss.graphql.core.schema.model;

import io.joss.graphql.core.decl.GQLScalarTypeDeclaration;
import io.joss.graphql.core.schema.TypeVisitors;
import io.joss.graphql.core.schema.TypeVisitors.NoReturnVisitor;

public final class ScalarType extends AbstractType implements InputCompatibleType {

  ScalarType(TypeBuilder typebuilder, Model model, String name, GQLScalarTypeDeclaration decl) {
    super(typebuilder, model, name, decl);
  }

  @Override
  public <R> R apply(TypeVisitors.GenericReturnVisitor<R> visitor) {
    return visitor.visitScalarType(this);
  }

  @Override
  public void apply(NoReturnVisitor visitor) {
    visitor.visitScalarType(this);
  }

}
