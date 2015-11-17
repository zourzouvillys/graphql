package io.jgql.core.reflect;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class ReflectionUtilsTest
{

  @Target({ ElementType.TYPE_USE })
  @Documented
  @Retention(RetentionPolicy.RUNTIME)
  public static @interface MyAnnotation
  {
  }

  public static class MyOtherClass<T>
  {
  }

  public static class MyTestClass
  {


    public List<@MyAnnotation Collection<String>> passes()
    {
      return null;
    }

    public List<@MyAnnotation MyOtherClass<String>> fails()
    {
      return null;
    }

  }

  @Test
  public void test() throws Exception
  {
    Assert.assertNotNull("WTF", get("passes").getAnnotatedActualTypeArguments()[0].getAnnotation(MyAnnotation.class));
    // Assert.assertNotNull("still broken", get("fails").getAnnotatedActualTypeArguments()[0].getAnnotation(MyAnnotation.class));
  }

  AnnotatedParameterizedType get(String name) throws NoSuchMethodException, SecurityException
  {
    Method method = MyTestClass.class.getMethod(name);
    return (AnnotatedParameterizedType) method.getAnnotatedReturnType();
  }

}
