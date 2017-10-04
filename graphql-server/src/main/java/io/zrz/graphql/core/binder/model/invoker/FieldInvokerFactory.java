package io.zrz.graphql.core.binder.model.invoker;

import io.zrz.graphql.core.binder.execution.QueryEnvironment;
import io.zrz.graphql.core.binder.model.OutputClassField;
import io.zrz.graphql.core.binder.reflect.TypedMethod;
import io.zrz.graphql.core.binder.runtime.DataContext;

/**
 * provides an instance of the right {@link FieldInvoker}.
 * 
 * @author theo
 *
 */

public class FieldInvokerFactory
{

  public static FieldInvoker bind(OutputClassField field, TypedMethod<?> method, DataContext ctx, QueryEnvironment env)
  {

    if (method.isStatic())
    {
      if (method.returnType().isCollection())
      {
        // it's a collection, so use the simple implementation.
        return new StaticSimpleInvoker(field, method, ctx);
      }
      else
      {
        return new StaticMethodInvoker(field, method, ctx, env);
      }
    }
    else
    {
      if (method.returnType().isCollection())
      {
        return new ObjectInvoker(field, method, ctx, env);
      }
      else {
        return new SingleValueFieldInvoker(field, method, ctx, env);
      }
    }

  }

}
