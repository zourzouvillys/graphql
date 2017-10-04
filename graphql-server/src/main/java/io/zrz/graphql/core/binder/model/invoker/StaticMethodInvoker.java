package io.zrz.graphql.core.binder.model.invoker;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import io.zrz.graphql.core.binder.execution.QueryEnvironment;
import io.zrz.graphql.core.binder.model.OutputClassField;
import io.zrz.graphql.core.binder.model.OutputClassFieldArg;
import io.zrz.graphql.core.binder.model.OutputClassFieldArg.BeanParamArg;
import io.zrz.graphql.core.binder.model.OutputClassFieldArg.ContextArg;
import io.zrz.graphql.core.binder.reflect.TypedMethod;
import io.zrz.graphql.core.binder.runtime.DataContext;
import io.zrz.graphql.core.binder.runtime.InputObserver;

/**
 * An invoker which passes all of the instances as a parameter to the given static method.
 * 
 * @author theo
 *
 */

public class StaticMethodInvoker extends AbstractInvoker
{

  private OutputClassField field;
  private TypedMethod<?> method;
  private DataContext ctx;
  private MethodHandle handle;
  private QueryEnvironment env;

  public StaticMethodInvoker(OutputClassField field, TypedMethod<?> method, DataContext ctx, QueryEnvironment env)
  {

    super(ctx, env);

    this.field = field;
    this.method = method;
    this.ctx = ctx;
    this.env = env;

    this.handle = makeHandle();

  }

  private MethodHandle makeHandle()
  {
    try
    {
      return adaptReturnType(super.addParameters(MethodHandles.publicLookup().unreflect(method.method()), field.params(), 0));
    }
    catch (Exception ex)
    {
      throw new RuntimeException(ex);
    }
  }

  private MethodHandle adaptReturnType(MethodHandle unreflect)
  {
    return unreflect;
  }

  @Override
  public InputObserver<Object, Object> open()
  {
    try
    {
      return (InputObserver<Object, Object>) handle.invoke();
    }
    catch (Throwable e)
    {
      throw new RuntimeException(e);
    }
  }

}
