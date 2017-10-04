package io.zrz.graphql.core.binder.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;

public class TypedParameter<S>
{

  private TypedClass<?> type;
  private Annotation[] annotations;
  private Parameter param;
  private int index;

  public TypedParameter(TypedMethod<?> method, TypedClass<?> type, Annotation[] ants, Parameter p, int index)
  {
    this.type = type;
    this.annotations = ants;
    this.param = p;
    this.index = index;
  }

  public TypedClass<?> type()
  {
    return type;
  }

  public <T extends Annotation> T getAnnotation(Class<T> klass)
  {

    for (Annotation a : annotations)
    {
      if (klass.isAssignableFrom(a.getClass()))
      {
        return klass.cast(a);
      }
    }

    return null;
  }

  public String getName()
  {
    return this.param.getName();
  }

  public <T extends Annotation> boolean hasAnnotation(Class<T> klass)
  {
    return getAnnotation(klass) != null;
  }

  public int index()
  {
    return index;
  }

}
