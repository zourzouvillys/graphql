package io.zrz.graphql.core.binder.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;

public class ParameterizedTypedClass<S> implements TypedClass<S>
{

  private TypedClass<?> klass;
  private TypedClass<?>[] params;
  private Annotation[] annotations;
  private ParameterizedType type;

  ParameterizedTypedClass(TypedClass<?> klass, TypedClass<?>[] params, ParameterizedType type, Annotation[] annotations)
  {
    this.klass = klass;
    this.params = params;
    this.type = type;
    this.annotations = annotations;
  }

  /**
   * we're only a collection if the inner type is a Collection.
   */

  public boolean isCollection()
  {
    if (klass instanceof SimpleTypedClass<?>)
    {
      return Collection.class.isAssignableFrom(((SimpleTypedClass<?>) klass).getType());
    }
    return false;
  }

  /**
   * 
   * @param method
   * @return
   */

  public TypedMethod<?> getMethod(Method method)
  {
    return new TypedMethod(this, method);
  }

  public String toString()
  {
    return new StringBuilder("ParameterizedTypedClass(").append(Arrays.asList(annotations)).append(")[").append(klass).append(", args=")
        .append(Arrays.asList(this.params))
        .append("]").toString();
  }

  @Override
  public TypedCollection<?> asCollection()
  {
    if (!isCollection())
    {
      throw new IllegalStateException();
    }
    return new TypedCollection<>(this.params[0]);
  }

  @Override
  public Collection<TypedMethod<?>> getDeclaredMethods()
  {
    return this.klass.getDeclaredMethods();
  }

  @Override
  public Annotation[] getAnnotations()
  {
    return annotations;
  }

  @Override
  public Type getType()
  {
    return type;
  }

  @Override
  public <T extends Annotation> boolean hasAnnotation(Class<T> annotation)
  {
    return getAnnotation(annotation) != null;
  }

  @Override
  public <T extends Annotation> T getAnnotation(Class<T> type)
  {
    for (Annotation ant : annotations)
    {
      if (type.isAssignableFrom(ant.getClass()))
      {
        return type.cast(ant);
      }
    }
    return null;
  }

  @Override
  public TypedGetter<?>[] getters()
  {
    return this.klass.getters();
  }

  @Override
  public Class<?> rawClass()
  {
    return klass.rawClass();
  }

  public TypedClass<?> parameter(int i)
  {
    return this.params[i];
  }
  
  @Override
  public TypedGetter<?> getter(String name)
  {
    return Arrays.stream(getters()).filter(a -> a.name().equals(name)).findAny().orElse(null);
  }


}
