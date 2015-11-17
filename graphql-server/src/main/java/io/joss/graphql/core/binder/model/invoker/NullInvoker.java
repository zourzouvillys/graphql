package io.joss.graphql.core.binder.model.invoker;

import io.joss.graphql.core.binder.model.OutputClassField;
import io.joss.graphql.core.binder.runtime.DataContext;
import io.joss.graphql.core.binder.runtime.InputObserver;
import io.joss.graphql.core.binder.runtime.OutputObserver;

public class NullInvoker implements FieldInvoker
{

  public NullInvoker(OutputClassField field, DataContext ctx)
  {
    // TODO Auto-generated constructor stub
  }

  @Override
  public InputObserver<Object, Object> open()
  {
    
    return new InputObserver<Object, Object>() {

      @Override
      public void onParent(Object parent, OutputObserver<Object, Object> output)
      {
        output.onNext(parent, null);
        output.onComplete();
      }

      @Override
      public void onCompleted()
      {
      }
      
    };
  }

}
