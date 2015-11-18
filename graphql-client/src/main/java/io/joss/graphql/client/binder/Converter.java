package io.joss.graphql.client.binder;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import io.joss.graphql.core.binder.reflect.ParameterizedTypedClass;
import io.joss.graphql.core.binder.reflect.ReflectionUtils;
import io.joss.graphql.core.binder.reflect.TypedClass;
import io.joss.graphql.core.value.GQLListValue;
import io.joss.graphql.core.value.GQLObjectValue;
import io.joss.graphql.core.value.GQLValue;
import io.joss.graphql.core.value.GQLValueConverters;

public class Converter
{

  private GQLValue value;
  private TypedClass<?> type;

  public Converter(GQLValue value, TypedClass<?> type)
  {
    this.value = value;
    this.type = type;
  }

  public Object dispatch(Object proxy, Method method, Object[] args)
  {

    TypedClass<?> type = ReflectionUtils.wrap(method.getAnnotatedReturnType());

    if (method.getName().equals("toString"))
    {
      return value.toString();
    }
    else if (method.getName().equals("equals"))
    {
      return false;
    }

    GQLValue value = ((GQLObjectValue) this.value).entries().get(method.getName());

    if (value == null)
    {
      return null;
    }

    return convert(value, type);

  }

  Object convert(GQLValue value, TypedClass<?> returnType)
  {

    if (returnType.rawClass().isPrimitive() || returnType.rawClass().equals(String.class))
    {
      return convert(value, returnType.rawClass());
    }
    else if (returnType.isCollection())
    {

      GQLListValue list = (GQLListValue)value;

      List<Object> collection = new LinkedList<>();

      TypedClass<?> componentType = returnType.asCollection().componentType();

      for (GQLValue item : list.values())
      {
        collection.add(convert(item, componentType));
      }

      return collection;
    }

    Converter c = new Converter(value, type);

    return Proxy.newProxyInstance(
        getClass().getClassLoader(),
        new Class<?>[] { returnType.rawClass() },
        c::dispatch);

  }

  /**
   * Convert the given GQL value to the given type.
   * 
   * @param value
   * @param type
   * 
   * @return
   */

  private Object convert(GQLValue value, Class<?> type)
  {

    if (type.equals(Integer.TYPE) || type.equals(Integer.class))
    {
      return value.apply(GQLValueConverters.intConverter());
    }
    else if (type.equals(Long.TYPE) || type.equals(Long.class))
    {
      return value.apply(GQLValueConverters.longConverter());
    }
    else if (type.equals(Boolean.TYPE) || type.equals(Boolean.class))
    {
      return value.apply(GQLValueConverters.booleanConverter());
    }
    else if (type.equals(String.class))
    {
      return value.apply(GQLValueConverters.stringConverter());
    }

    throw new RuntimeException(String.format("Don't know how to convert %s to %s", value.getClass().getSimpleName(), type.getSimpleName()));

  }

}
