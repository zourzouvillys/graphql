package io.zrz.graphql.core.binder.model;

import java.util.LinkedList;
import java.util.List;

import io.zrz.graphql.core.binder.reflect.ReflectionUtils;
import io.zrz.graphql.core.binder.reflect.TypedClass;
import io.zrz.graphql.core.binder.reflect.TypedGetter;

public class InputClassBinding
{

  private TypedClass<?> type;

  InputClassBinding(TypedClass<?> typedClass)
  {
    this.type = typedClass;
  }

  public static InputClassBinding bind(Class<?> type)
  {
    if (type == null)
    {
      throw new IllegalArgumentException("type");
    }
    return new InputClassBinding(ReflectionUtils.forClass(type));
  }

  public List<InputClassField> getters()
  {

    List<InputClassField> inputs = new LinkedList<>();

    for (TypedGetter<?> getter : type.getters())
    {
      inputs.add(new InputClassField(getter));
    }

    return inputs;
  }

}
