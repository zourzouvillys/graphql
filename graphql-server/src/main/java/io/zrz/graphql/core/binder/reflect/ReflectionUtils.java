package io.zrz.graphql.core.binder.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedArrayType;
import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ReflectionUtils
{

  public static <T> TypedClass<T> forClass(Class<?> klass)
  {
    return wrap(klass, klass.getAnnotations());
  }
  
  public static <T> TypedClass<T> wrap(Type klass)
  {
    return wrap(klass, new Annotation[0]);
  }

  public static <T> TypedClass<T> wrap(Type type, Annotation[] annotations)
  {

    if (type == null)
      throw new IllegalArgumentException();

    if (type instanceof Class<?>)
    {

      return new SimpleTypedClass<T>((Class<?>) type, annotations);

    }
    else if (type instanceof ParameterizedType)
    {

      ParameterizedType t = (ParameterizedType) type;

      TypedClass<?> inner = wrap(t.getRawType());

      List<TypedClass<?>> params = new LinkedList<>();

      for (Type at : ((ParameterizedType) type).getActualTypeArguments())
      {
        params.add(wrap(at));
      }

      return new ParameterizedTypedClass<T>(inner, params.toArray(new TypedClass<?>[0]), t, annotations);

    }
    else if (type instanceof WildcardType)
    {
      throw new RuntimeException("WildcardType not supported yet");
    }

    throw new RuntimeException("Unknown Type: " + type.getClass());

  }

  public static <T> TypedClass<T> wrap(AnnotatedType ant)
  {

    if (ant instanceof AnnotatedParameterizedType)
    {
      
      AnnotatedParameterizedType pant = (AnnotatedParameterizedType) ant;
      ParameterizedType pt = (ParameterizedType) ant.getType();
      return (TypedClass<T>) wrap(wrapSimple(pt.getRawType()), pant.getAnnotatedActualTypeArguments(), pt, ant.getAnnotations());
    }
    else if (ant instanceof AnnotatedArrayType)
    {

      AnnotatedArrayType aant = (AnnotatedArrayType) ant;
      
      TypedClass<?> ctype = wrap(aant.getAnnotatedGenericComponentType());
      
      return new TypedArrayClass<>(ctype, ant.getAnnotations());
      
    }

    return wrap(ant.getType(), ant.getDeclaredAnnotations());
    
  }

  private static SimpleTypedClass<?> wrapSimple(Type type)
  {
    return new SimpleTypedClass<>((Class<?>) type, new Annotation[0]);
  }

  /**
   *
   * @param type
   * @param annotatedActualTypeArguments
   * @param annotations
   * @return
   */

  private static TypedClass<?> wrap(TypedClass<?> raw, AnnotatedType[] args, ParameterizedType type, Annotation[] annotations)
  {

    return new ParameterizedTypedClass<>(
        raw,
        Arrays.asList(args).stream().map(ReflectionUtils::wrap).toArray(len -> new TypedClass<?>[len]),
        type,
        annotations);

  }

  /**
   * See the associated test case. This attempts to catch any usage of it to save wasting debugging time.
   * 
   * @param method
   * @return
   */

  public static void throwIfTriggersJDKBug(Method method)
  {

    if (!(method.getGenericReturnType() instanceof ParameterizedType))
    {
      return;
    }

    ParameterizedType type = (ParameterizedType) method.getGenericReturnType();

    for (Type arg : type.getActualTypeArguments())
    {
      if (throwIfTriggersJDKBug(arg))
      {
        System.err.println(
            String.format("%s (used as return type of %s) is an inner class, and TYPE_USE annotations aren't returned.  Move %s to a non inner class to avoid this.",
                arg,
                method,
                arg));
      }
    }

  }

  private static boolean throwIfTriggersJDKBug(Type arg)
  {
    if (arg instanceof Class<?>)
    {
      if (((Class<?>) arg).getDeclaringClass() != null)
      {
        return true;
      }
    }
    else if (arg instanceof ParameterizedType)
    {

      ParameterizedType type = (ParameterizedType) arg;

      for (Type inner : type.getActualTypeArguments())
      {
        if (throwIfTriggersJDKBug(inner))
        {
          return true;
        }

      }
    }
    return false;
  }

}
