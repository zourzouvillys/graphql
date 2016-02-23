package io.joss.graphql.executor;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.google.common.reflect.Parameter;

import io.joss.graphql.core.binder.annotatons.GQLArg;
import io.joss.graphql.core.binder.annotatons.GQLContext;
import io.joss.graphql.executor.MethodBinder.ParameterProvider;

public class MethodBinderTest
{

  public String testMethod(@GQLArg("hello") String hello, int val, @GQLContext String somethingElse)
  {
    return hello;
  }

  @Test
  public void test() throws Exception
  {
    MethodBinder<MethodBinderTest, String> binder = MethodBinder
        .bind(MethodBinderTest.class, getClass().getMethod("testMethod", String.class, Integer.TYPE, String.class))
        .returning(String.class);

    String result = binder.invoke(this, new ParameterProvider() {

      @Override
      public Object named(String name, Parameter p)
      {
        return name;
      }

      @Override
      public Object context(Parameter p)
      {
        return "ohnoes";
      }

      @Override
      public Object positional(int position, Parameter p)
      {
        return position;
      }

    });

    assertEquals(result, "hello");

  }

}
