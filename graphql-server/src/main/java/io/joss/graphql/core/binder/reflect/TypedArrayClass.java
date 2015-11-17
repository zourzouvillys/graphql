package io.joss.graphql.core.binder.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class TypedArrayClass<T> implements TypedClass<T>
{

  private TypedClass<?> component;
  private Annotation[] annotations;

  public TypedArrayClass(TypedClass<?> component, Annotation[] annotations)
  {
    this.component = component;
    this.annotations = annotations;
  }
  
  @Override
  public TypedMethod<?> getMethod(Method method)
  {
    return null;
  }

  @Override
  public boolean isCollection()
  {
    return true;
  }

  @Override
  public TypedCollection<T> asCollection()
  {
    return new TypedCollection<>(component);
  }

  @Override
  public Collection<TypedMethod<?>> getDeclaredMethods()
  {
    return Collections.emptyList();
  }

  @Override
  public Annotation[] getAnnotations()
  {
    return annotations;
  }

  @Override
  public Type getType()
  {
    // TODO Auto-generated method stub
    throw new RuntimeException();
  }

  @Override
  public <R extends Annotation> boolean hasAnnotation(Class<R> annotation)
  {
    return getAnnotation(annotation) != null;
  }

  @Override
  public <R extends Annotation> R getAnnotation(Class<R> type)
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
    return new TypedGetter<?>[0];
  }

  @Override
  public Class<?> rawClass()
  {
    return component.rawClass();
  }

  @Override
  public TypedGetter<?> getter(String name)
  {
    return Arrays.stream(getters()).filter(a -> a.name().equals(name)).findAny().orElse(null);
  }

}
