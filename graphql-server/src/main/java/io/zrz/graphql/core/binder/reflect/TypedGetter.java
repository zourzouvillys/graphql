package io.zrz.graphql.core.binder.reflect;

import java.lang.annotation.Annotation;

import io.zrz.graphql.core.binder.annotatons.GQLDefaultValue;

public interface TypedGetter<S>
{

  String name();

  TypedClass<?> type();

  <R extends Annotation> R getAnnotation(Class<R> type);

  <R extends Annotation> Annotation[] getAnnotations();

  <R extends Annotation> boolean hasAnnotation(Class<R> type);

  default String defaultValue()
  {
    if (hasAnnotation(GQLDefaultValue.class))
      return getAnnotation(GQLDefaultValue.class).value();
    return null;
  }

}
