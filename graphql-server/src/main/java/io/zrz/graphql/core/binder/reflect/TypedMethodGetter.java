package io.zrz.graphql.core.binder.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TypedMethodGetter<T> implements TypedGetter<T>
{

  private SimpleTypedClass<T> declaring;
  private Method method;
  private String name;
  private Field backing;

  public TypedMethodGetter(SimpleTypedClass<T> declaring, Method method, String name)
  {
    this.declaring = declaring;
    this.method = method;
    this.name = name;

    // if we have a field on the class with same name, use the annotations from it (helps with lombok usage)

    for (Field f : method.getDeclaringClass().getDeclaredFields())
    {
      if (f.getName().equals(name))
      {
        try
        {
          this.backing = method.getDeclaringClass().getDeclaredField(name);
          backing.setAccessible(true);
          break;
        }
        catch (NoSuchFieldException | SecurityException e)
        {
          e.printStackTrace();
        }
      }
    }

  }

  /**
   * returns a class which represents the type usage of the value of the return.
   * 
   * @return
   */

  @Override
  public TypedClass<T> type()
  {
    return ReflectionUtils.wrap((backing != null) ? backing.getAnnotatedType() : method.getAnnotatedReturnType());
  }

  @Override
  public String toString()
  {
    return String.format("method %s %s.%s", type(), declaring, method.getName());
  }

  @Override
  public String name()
  {
    return name;
  }

  @Override
  public <R extends Annotation> R getAnnotation(Class<R> type)
  {
    R val = method.getAnnotation(type);
    if (val == null && this.backing != null)
    {
      return this.backing.getAnnotation(type);
    }
    return val;
  }

  @Override
  public Annotation[] getAnnotations()
  {
    List<Annotation> ants = new ArrayList<>();
    for (Annotation ant : method.getAnnotations())
    {
      ants.add(ant);
    }
    if (backing != null)
    {
      for (Annotation ant : backing.getAnnotations())
      {
        ants.add(ant);
      }
    }
    return ants.toArray(new Annotation[0]);
  }

  @Override
  public <R extends Annotation> boolean hasAnnotation(Class<R> type)
  {
    return getAnnotation(type) != null;
  }

}
