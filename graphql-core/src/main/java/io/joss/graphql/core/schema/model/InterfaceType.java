package io.joss.graphql.core.schema.model;

import io.joss.graphql.core.schema.TypeVisitors;
import io.joss.graphql.core.schema.TypeVisitors.NoReturnVisitor;

public class InterfaceType extends AbstractType {

  InterfaceType(TypeBuilder typebuilder, Model model, String name) {
    super(typebuilder, model, name);
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
