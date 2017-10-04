package io.zrz.graphql.core.binder.model;

import io.zrz.graphql.core.binder.reflect.TypedClass;
import io.zrz.graphql.core.binder.reflect.TypedGetter;

public class InputClassField
{

  private TypedGetter<?> getter;

  public InputClassField(TypedGetter<?> getter)
  {
    this.getter = getter;
  }

  public String name()
  {
    return getter.name();
  }

  public TypedClass<?> type()
  {
    return getter.type();
  }

}
