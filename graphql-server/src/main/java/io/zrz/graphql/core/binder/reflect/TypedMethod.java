package io.zrz.graphql.core.binder.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

public class TypedMethod<R>
{

  private TypedClass<?> container;
  private Method method;

  public TypedMethod(TypedClass<?> container, Method method)
  {
    this.container = container;
    this.method = method;
  }

  public boolean isStatic()
  {
    return Modifier.isStatic(method.getModifiers());
  }

  /**
   * the return type of this method.
   * 
   * a bug in (at least) JDK 1.8r65 doesn't return annotations if the parameterized type is an inner class. to save hours of debugging we
   * throw an exception if someone attempts to do that.
   * 
   * @return
   */

  public TypedClass<R> returnType()
  {
    ReflectionUtils.throwIfTriggersJDKBug(method);
    return (TypedClass<R>) ReflectionUtils.wrap(method.getAnnotatedReturnType());
  }

  /**
   * if this method is overriding one in a base class or interface.
   * 
   * @return
   */

  public boolean isOverride()
  {
    return method.getAnnotation(Override.class) != null;
  }

  public <T extends Annotation> T getDeclaredAnnotation(Class<T> type)
  {
    return method.getDeclaredAnnotation(type);
  }

  public String getName()
  {
    return method.getName();
  }

  public TypedParameter<?>[] params()
  {

    List<TypedParameter<?>> ret = new ArrayList<>(method.getParameterCount()); 

    AnnotatedType[] types = method.getAnnotatedParameterTypes();
    Annotation[][] ants = method.getParameterAnnotations();
    Parameter[] params = method.getParameters();

    for (int i = 0; i < method.getParameterCount(); ++i)
    {
      TypedClass<Object> type = ReflectionUtils.wrap(types[i]);
      ret.add(new TypedParameter<>(this, type, ants[i], params[i], i));
    }

    return ret.toArray(new TypedParameter<?>[0]);

  }

  public String toString()
  {
    return this.method.toGenericString();
  }

  public Method method()
  {
    return this.method;
  }

  public <A extends Annotation> boolean hasAnnotation(Class<A> klass)
  {
    return method.getAnnotation(klass) != null;
  }

}
