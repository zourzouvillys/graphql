package io.joss.graphql.core.schema.model;

import java.util.Collection;

import io.joss.graphql.core.decl.GQLInterfaceTypeDeclaration;
import io.joss.graphql.core.schema.TypeVisitors;
import io.joss.graphql.core.schema.TypeVisitors.NoReturnVisitor;

public class InterfaceType extends AbstractType {

  InterfaceType(TypeBuilder typebuilder, Model model, String name, GQLInterfaceTypeDeclaration decl, Collection<GQLInterfaceTypeDeclaration> exts) {
    super(typebuilder, model, name, decl, exts);
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
