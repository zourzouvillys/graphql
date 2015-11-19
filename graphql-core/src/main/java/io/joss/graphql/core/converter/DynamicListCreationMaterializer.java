package io.joss.graphql.core.converter;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

import io.joss.graphql.core.value.GQLListValue;
import io.joss.graphql.core.value.GQLValue;

public class DynamicListCreationMaterializer implements TypeMaterializer<GQLListValue>
{

  @SuppressWarnings("unchecked")
  @Override
  public <O> O convert(TypeConverter converter, GQLListValue from, Type targetType, Annotation[] annotations)
  {
    
    ParameterizedType type = (ParameterizedType)targetType;
    
    List<Object> child = new LinkedList<>(); 

    for (GQLValue i : from.values())
    {
      child.add(converter.convert(i, type.getActualTypeArguments()[0]));
    }

    return (O) child;
    
  }

}
