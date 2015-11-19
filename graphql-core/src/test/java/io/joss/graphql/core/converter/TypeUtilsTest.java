package io.joss.graphql.core.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.AnnotatedType;
import java.util.Collection;
import java.util.List;

import org.junit.Test;

public class TypeUtilsTest
{

  public static interface MyService<T>
  {
  }

  public abstract static class MyClass implements MyService<String>, Collection<Integer>
  {
  }

  @Test
  public void testRawTypeEquals()
  {
    assertTrue(TypeUtils.isRawEqual(MyClass.class.getGenericInterfaces()[0], MyService.class));
  }

  @Test
  public void test()
  {
    AnnotatedType type = TypeUtils.getParamterOfInterface(MyClass.class, MyService.class, 0);
    assertEquals(type.getType(), String.class);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidParamNumberThrows()
  {
    TypeUtils.getParamterOfInterface(MyClass.class, Collection.class, 1);
  }

  @Test
  public void testNotFoundReturnsNull()
  {
    assertNull(TypeUtils.getParamterOfInterface(MyClass.class, List.class, 0));
  }

}
