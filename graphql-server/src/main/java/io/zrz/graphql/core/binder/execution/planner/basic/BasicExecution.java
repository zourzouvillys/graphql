package io.zrz.graphql.core.binder.execution.planner.basic;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import io.zrz.graphql.core.binder.BindingProvider;
import io.zrz.graphql.core.binder.execution.QueryEnvironment;
import io.zrz.graphql.core.binder.model.OutputClassBinding;
import io.zrz.graphql.core.binder.model.OutputClassField;
import io.zrz.graphql.core.binder.runtime.DataContext;
import io.zrz.graphql.core.binder.runtime.InputObserver;
import io.zrz.graphql.core.binder.runtime.OutputObserver;
import io.zrz.graphql.core.value.GQLObjectValue;
import io.zrz.graphql.core.value.GQLValue;
import io.zrz.graphql.core.value.GQLValues;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BasicExecution
{

  private BindingProvider binder;
  private QueryEnvironment env;
  private DataContext ctx;
  private Consumer<GQLValue> result;

  public BasicExecution(BindingProvider binder, QueryEnvironment env, DataContext ctx, Consumer<GQLValue> result)
  {
    this.binder = binder;
    this.env = env;
    this.ctx = ctx;
    this.result = result;
  }

  /**
   * initial root execution.
   */

  public void execute(Object root)
  {
    result.accept(executeFields(root, ctx));
  }

  /**
   * execute a specific context.
   * 
   * @param path
   * @return
   */

  private GQLValue executeFields(Object instance, DataContext path)
  {

    GQLObjectValue.Builder b = GQLObjectValue.builder();

    OutputClassBinding me = binder.meta(path.declaration());

    for (DataContext ctx : path.children())
    {
      b.value(ctx.alias(), executeField(me, instance, ctx));
    }

    return b.build();

  }

  /**
   * executes a specific field on the instance instance indicated by the fieldSelection parameter.
   * 
   * @param instance
   * @param fieldSelection
   * @return
   */

  private GQLValue executeField(OutputClassBinding me, Object instance, DataContext fieldSelection)
  {

    OutputClassField field = me.fields(fieldSelection.name()).get(0);

    InputObserver<Object, Object> input = field.invoker(fieldSelection, this.env).open();

    AtomicReference<GQLValue> ref = new AtomicReference<GQLValue>();

    if (fieldSelection.isCollection())
    {

      input.onParent(instance, new OutputObserver<Object, Object>() {

        List<GQLValue> results = new LinkedList<>();

        @Override
        public void onNext(Object parent, Object child)
        {
          results.add(applyField(child, fieldSelection));
        }

        @Override
        public void onComplete()
        {
          ref.set(GQLValues.listValue(results));
        }

      });

    }
    else
    {

      input.onParent(instance, new OutputObserver<Object, Object>() {

        @Override
        public void onNext(Object parent, Object child)
        {
          if (ref.get() != null)
          {
            log.info("Field selection was {}", fieldSelection);
            log.info("Field type: {}", fieldSelection.type());
            log.info("Field return shape: {}", fieldSelection.returnShape());
            log.info("Invoker: {}", field);
            log.info("Invoker return type: {}", field.returnType());
            throw new RuntimeException("Invoker " + input + " returned multiple values - only expected one!");
          }
          ref.set(applyField(child, fieldSelection));
        }

        @Override
        public void onComplete()
        {
        }

      });

    }

    input.onCompleted();

    return ref.get();

  }

  /**
   * convert the field handler's result into a GQL result (or descend).
   * 
   * @param child
   * @param ctx
   * @return
   */

  private GQLValue applyField(Object child, DataContext ctx)
  {
    if (child == null)
    {
      // we don't apply to anything that's null.
      return null;
    }
    if (ctx.isLeaf())
    {
      return GQLValues.stringValue(child.toString());
    }
    else if (ctx.isCollection())
    {
      return executeFields(child, ctx);
    }
    return executeFields(child, ctx);
  }

}
