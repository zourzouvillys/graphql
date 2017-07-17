package io.joss.graphql.core.schema.model;

import io.joss.graphql.core.schema.TypeVisitors;
import io.joss.graphql.core.schema.TypeVisitors.NoReturnVisitor;

public class EnumType extends AbstractType implements InputCompatibleType {

  protected EnumType(TypeBuilder typebuilder, Model model, String name) {
    super(typebuilder, model, name);
  }

  @Override
  public <R> R apply(TypeVisitors.GenericReturnVisitor<R> visitor) {
    return visitor.visitEnumType(this);
  }

  @Override
  public void apply(NoReturnVisitor visitor) {
    visitor.visitEnumType(this);
  }

}
