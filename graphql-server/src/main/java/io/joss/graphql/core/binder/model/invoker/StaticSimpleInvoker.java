package io.joss.graphql.core.binder.model.invoker;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.util.LinkedList;
import java.util.List;

import io.joss.graphql.core.binder.model.OutputClassField;
import io.joss.graphql.core.binder.reflect.TypedMethod;
import io.joss.graphql.core.binder.runtime.DataContext;
import io.joss.graphql.core.binder.runtime.InputObserver;
import io.joss.graphql.core.binder.runtime.OutputObserver;
import lombok.Value;

/**
 * An invoker which takes an array (or nothing) as the input instances, and returns a collection, iterator, or array.
 * 
 * @author theo
 *
 */

public class StaticSimpleInvoker implements FieldInvoker
{

  private OutputClassField field;
  private TypedMethod<?> method;
  private DataContext ctx;
  private MethodHandle handle;

  public StaticSimpleInvoker(OutputClassField field, TypedMethod<?> method, DataContext ctx)
  {
    this.field = field;
    this.method = method;
    this.ctx = ctx;
    this.handle = makeHandle();
  }

  private MethodHandle makeHandle()
  {
    try
    {
      return adaptReturnType(MethodHandles.publicLookup().unreflect(method.method()));
    }
    catch (Exception ex)
    {
      throw new RuntimeException(ex);
    }
  }

  private MethodHandle adaptReturnType(MethodHandle unreflect)
  {
    return null;
  }

  private void dispatch(List<Correlation> inputs)
  {
  }

  @Value
  private static class Correlation
  {
    Object input;
    OutputObserver<Object, Object> handler;
  }

  @Override
  public InputObserver<Object, Object> open()
  {

    return new InputObserver<Object, Object>() {

      List<Correlation> inputs = new LinkedList<>();

      @Override
      public void onParent(Object parent, OutputObserver<Object, Object> output)
      {
        inputs.add(new Correlation(parent, output));
      }

      @Override
      public void onCompleted()
      {
        dispatch(inputs);
      }

    };
  }

}
