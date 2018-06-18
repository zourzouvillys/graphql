package io.zrz.graphql.core.binder.model.invoker;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.util.Collection;

import io.zrz.graphql.core.binder.execution.QueryEnvironment;
import io.zrz.graphql.core.binder.model.OutputClassField;
import io.zrz.graphql.core.binder.model.OutputClassFieldArg;
import io.zrz.graphql.core.binder.model.OutputClassFieldArg.BeanParamArg;
import io.zrz.graphql.core.binder.reflect.TypedMethod;
import io.zrz.graphql.core.binder.runtime.DataContext;
import io.zrz.graphql.core.binder.runtime.InputObserver;
import io.zrz.graphql.core.binder.runtime.OutputObserver;
import io.zrz.graphql.core.lang.GQLTypeRegistry;

public class SingleValueFieldInvoker extends AbstractInvoker
{

  private OutputClassField field;
  private TypedMethod<?> method;
  private DataContext ctx;
  private MethodHandle handle;

  public SingleValueFieldInvoker(OutputClassField field, TypedMethod<?> method, DataContext ctx, QueryEnvironment env)
  {
    super(ctx, env);
    this.field = field;
    this.method = method;
    this.ctx = ctx;
    this.handle = makeHandle();
  }

  public TypedMethod<?> method()
  {
    return this.method;
  }

  private MethodHandle makeHandle()
  {
    try
    {
      return adaptReturnType(addParameters(MethodHandles.publicLookup().unreflect(method.method()), field.params(), 1));
    }
    catch (Exception ex)
    {
      throw new RuntimeException(ex);
    }
  }

  private MethodHandle adaptReturnType(MethodHandle handle)
  {
    return handle;
  }

  @Override
  public InputObserver<Object, Object> open()
  {

    return new InputObserver<Object, Object>() {

      @Override
      public void onParent(Object parent, OutputObserver<Object, Object> output)
      {
        try
        {
          Object val = handle.bindTo(parent).invoke();
          output.onNext(parent, val);
        }
        catch (Throwable t)
        {
          throw new RuntimeException(t);
        }
        finally
        {
          output.onComplete();
        }
      }

      @Override
      public void onCompleted()
      {
      }

    };

  }

  public String toString()
  {
    return method().getName();
  }

}