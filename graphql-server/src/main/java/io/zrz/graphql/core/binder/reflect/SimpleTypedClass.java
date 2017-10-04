package io.zrz.graphql.core.binder.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * An unparameterized class.
 * 
 * @author theo
 *
 */

public class SimpleTypedClass<T> implements TypedClass<T>
{

  private Class<?> klass;
  private Annotation[] annotations;

  public SimpleTypedClass(Class<?> klass, Annotation[] annotations)
  {
    this.klass = klass;
    this.annotations = annotations;
  }

  @Override
  public TypedMethod<?> getMethod(Method method)
  {
    return new TypedMethod<>(this, method);
  }

  @Override
  public boolean isCollection()
  {
    return klass.isArray();
  }

  public Class<?> getType()
  {
    return klass;
  }

  @Override
  public TypedCollection<?> asCollection()
  {
    if (!klass.isArray())
    {
      throw new IllegalStateException();
    }
    return new TypedCollection<>(ReflectionUtils.wrap(klass.getComponentType()));
  }

  public String toString()
  {
    return new StringBuilder("Simple(").append(Arrays.asList(annotations)).append(")[").append(klass.getName()).append("]").toString();
  }

  @Override
  public Collection<TypedMethod<?>> getDeclaredMethods()
  {
    return Arrays.stream(this.klass.getDeclaredMethods()).map(m -> new TypedMethod<>(this, m)).collect(Collectors.toList());
  }

  @Override
  public Annotation[] getAnnotations()
  {
    return annotations;
  }

  @Override
  public <A extends Annotation> boolean hasAnnotation(Class<A> annotation)
  {
    return getAnnotation(annotation) != null;
  }

  @Override
  public <A extends Annotation> A getAnnotation(Class<A> type)
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

  /**
   * 
   */

  @Override
  public TypedGetter<?>[] getters()
  {

    Map<String, TypedGetter<?>> ret = new LinkedHashMap<>();

    for (Field field : this.klass.getFields())
    {

      if (!Modifier.isPublic(field.getModifiers()))
      {
        continue;
      }

      TypedFieldGetter<?> f = new TypedFieldGetter<>(this, field);
      ret.put(f.name(), f);

    }

    for (Method method : klass.getMethods())
    {

      if (method.getDeclaringClass().equals(Object.class))
      {
        continue;
      }

      if (!Modifier.isPublic(method.getModifiers()))
      {
        continue;
      }

      final TypedMethodGetter<?> f;

      if (method.getName().startsWith("get"))
      {
        f = new TypedMethodGetter<>(this, method, uncapitalize(method.getName().substring(3)));
      }
      else if ((method.getReturnType().equals(Boolean.TYPE) || method.getReturnType().equals(Boolean.class)) && method.getName().startsWith("is"))
      {
        f = new TypedMethodGetter<>(this, method, uncapitalize(method.getName().substring(2)));
      }
      else
      {
        f = null;
      }

      if (f != null)
      {

        if (ret.containsKey(f.name()))
        {
          // both the getter and field are accessable.
          continue;
        }

        ret.put(f.name(), f);

      }

    }

    return ret.values().toArray(new TypedGetter<?>[0]);

  }

  private String uncapitalize(String string)
  {
    return Character.toLowerCase(string.charAt(0)) + (string.length() > 1 ? string.substring(1) : "");
  }

  @Override
  public Class<?> rawClass()
  {
    // TODO Auto-generated method stub
    return klass;
  }

  @Override
  public TypedGetter<?> getter(String name)
  {
    return Arrays.stream(getters()).filter(a -> a.name().equals(name)).findAny().orElse(null);
  }

}
