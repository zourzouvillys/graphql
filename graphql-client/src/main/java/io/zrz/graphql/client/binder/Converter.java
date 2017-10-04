package io.zrz.graphql.client.binder;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import io.zrz.graphql.core.binder.reflect.ParameterizedTypedClass;
import io.zrz.graphql.core.binder.reflect.ReflectionUtils;
import io.zrz.graphql.core.binder.reflect.TypedClass;
import io.zrz.graphql.core.converter.TypeConverter;
import io.zrz.graphql.core.value.GQLListValue;
import io.zrz.graphql.core.value.GQLObjectValue;
import io.zrz.graphql.core.value.GQLValue;
import io.zrz.graphql.core.value.GQLValueConverters;

public class Converter
{

  private GQLValue value;
  private TypedClass<?> type;
  private TypeConverter converter;

  public Converter(TypeConverter converter, GQLValue value, TypedClass<?> type)
  {
    this.converter = converter;
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

    Converter c = new Converter(converter, value, type);

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
    return converter.convert(value, type);    
  }

}
