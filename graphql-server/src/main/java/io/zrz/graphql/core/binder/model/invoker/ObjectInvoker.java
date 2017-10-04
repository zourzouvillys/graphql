package io.zrz.graphql.core.binder.model.invoker;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.util.Collection;

import io.zrz.graphql.core.binder.execution.QueryEnvironment;
import io.zrz.graphql.core.binder.model.OutputClassField;
import io.zrz.graphql.core.binder.reflect.TypedMethod;
import io.zrz.graphql.core.binder.runtime.DataContext;
import io.zrz.graphql.core.binder.runtime.InputObserver;
import io.zrz.graphql.core.binder.runtime.OutputObserver;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ObjectInvoker extends AbstractInvoker
{

  private OutputClassField field;
  private TypedMethod<?> method;
  private DataContext ctx;
  private MethodHandle handle;

  public ObjectInvoker(OutputClassField field, TypedMethod<?> method, DataContext ctx, QueryEnvironment env)
  {
    super(ctx, env);
    this.field = field;
    this.method = method;
    this.ctx = ctx;
    this.handle = makeHandle();
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

      @SneakyThrows
      @Override
      public void onParent(Object parent, OutputObserver<Object, Object> output)
      {
        try
        {
          final Collection<?> ret;

          try
          {
            ret = (Collection<?>) handle.bindTo(parent).invoke();
          }
          catch (Exception ex)
          {
            log.error("ctx={}, handle={}, bound={}", ctx.path(), handle, parent, ex);
            throw new RuntimeException("Invalid Binding", ex);
          }

          try
          {
            for (Object o : ret)
            {
              output.onNext(parent, o);
            }
          }
          catch (Exception ex)
          {
            throw new RuntimeException("Upstream error", ex);
          }

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

}
