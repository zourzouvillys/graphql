package io.zrz.graphql.core.binder.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class TypedFieldGetter<T> implements TypedGetter<T>
{

  private SimpleTypedClass<T> declaring;
  private Field field;

  public TypedFieldGetter(SimpleTypedClass<T> declaring, Field field)
  {
    this.declaring = declaring;
    this.field = field;
  }

  public TypedClass<T> type()
  {
    return ReflectionUtils.wrap(field.getAnnotatedType());
  }

  public String toString()
  {
    return String.format("field %s %s.%s", type(), declaring, field.getName());
  }

  @Override
  public String name()
  {
    return field.getName();
  }

  @Override
  public <R extends Annotation> R getAnnotation(Class<R> type)
  {
    return field.getAnnotation(type);
  }

  @Override
  public <R extends Annotation> Annotation[] getAnnotations()
  {
    return field.getAnnotations();
  }

  @Override
  public <R extends Annotation> boolean hasAnnotation(Class<R> type)
  {
    return getAnnotation(type) != null;
  }

}
