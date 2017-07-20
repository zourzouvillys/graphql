package io.joss.graphql.core.schema.model;

import java.util.Collection;

import io.joss.graphql.core.decl.GQLUnionTypeDeclaration;
import io.joss.graphql.core.schema.TypeVisitors;
import io.joss.graphql.core.schema.TypeVisitors.NoReturnVisitor;

public class UnionType extends AbstractType {

  UnionType(TypeBuilder typebuilder, Model model, String name, GQLUnionTypeDeclaration decl, Collection<GQLUnionTypeDeclaration> exts) {
    super(typebuilder, model, name, decl, exts);
  }

  @Override
  public <R> R apply(TypeVisitors.GenericReturnVisitor<R> visitor) {
    return visitor.visitUnionType(this);
  }

  @Override
  public void apply(NoReturnVisitor visitor) {
    visitor.visitUnionType(this);
  }

}
