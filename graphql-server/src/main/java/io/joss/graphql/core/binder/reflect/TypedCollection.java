package io.joss.graphql.core.binder.reflect;

import java.util.Collection;
import java.util.List;

public class TypedCollection<S>
{

  private TypedClass<?> component;

  public TypedCollection(TypedClass<?> component)
  {
    this.component = component;
  }

  public TypedClass<?> componentType()
  {
    return component;
  }

}
