package io.joss.graphql.core.binder.model;

import io.joss.graphql.core.binder.reflect.TypedClass;
import io.joss.graphql.core.types.GQLDeclarationRef;

public class InterfaceRef
{

  private GQLDeclarationRef decl;
  private TypedClass<?> iface;

  public InterfaceRef(GQLDeclarationRef decl, TypedClass<?> iface)
  {
    this.decl = decl;
    this.iface = iface;
  }

  public TypedClass<?> type()
  {
    return this.iface;
  }

  public GQLDeclarationRef ref()
  {
    return decl;
  }

}
