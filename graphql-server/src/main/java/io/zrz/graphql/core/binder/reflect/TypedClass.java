package io.zrz.graphql.core.binder.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collection;

public interface TypedClass<S>
{

  TypedMethod<?> getMethod(Method method);

  /**
   * true if this is an array type or a Collection.
   * 
   * @return
   */

  boolean isCollection();

  TypedCollection<?> asCollection();

  Collection<TypedMethod<?>> getDeclaredMethods();

  Annotation[] getAnnotations();

  Type getType();

  <T extends Annotation> boolean hasAnnotation(Class<T> annotation);

  <T extends Annotation> T getAnnotation(Class<T> type);

  /**
   * returns any getters on the class - either public fields or getXXX ones.
   */

  TypedGetter<?>[] getters();

  TypedGetter<?> getter(String name);

  /**
   * Returns the raw class value for this type.
   * 
   * If the value is a class, then this is simply returned. If it's an array or collection, the element type is returned (which may be an
   * array iself).
   * 
   * If it's a parameterized type, the raw class type is returned.
   * 
   * 
   * @return
   */

  Class<?> rawClass();


}
