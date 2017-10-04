package io.zrz.graphql.core.binder.model.invoker;

import java.beans.ConstructorProperties;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import io.zrz.graphql.core.binder.execution.QueryEnvironment;
import io.zrz.graphql.core.binder.model.OutputClassFieldArg;
import io.zrz.graphql.core.binder.model.OutputClassFieldArg.BeanParamArg;
import io.zrz.graphql.core.binder.model.OutputClassFieldArg.ContextArg;
import io.zrz.graphql.core.binder.model.OutputClassFieldArg.InputArg;
import io.zrz.graphql.core.binder.reflect.TypedClass;
import io.zrz.graphql.core.binder.reflect.TypedGetter;
import io.zrz.graphql.core.binder.runtime.DataContext;
import io.zrz.graphql.core.decl.GQLArgumentDefinition;
import io.zrz.graphql.core.doc.GQLArgument;
import io.zrz.graphql.core.value.GQLValue;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractInvoker implements FieldInvoker
{

  private final DataContext ctx;
  private QueryEnvironment env;

  AbstractInvoker(DataContext ctx, QueryEnvironment env)
  {
    if (ctx == null)
    {
      throw new IllegalArgumentException("ctx");
    }
    if (env == null)
    {
      throw new IllegalArgumentException("env");
    }
    this.ctx = ctx;
    this.env = env;
  }

  /**
   * bake the parameters into the method handle.
   * 
   * @param handle
   * @param params
   * @return
   */

  protected MethodHandle addParameters(MethodHandle handle, List<OutputClassFieldArg> params, int index)
  {

    for (OutputClassFieldArg param : params)
    {

      if (param instanceof OutputClassFieldArg.InputArg)
      {

        OutputClassFieldArg.InputArg ip = (InputArg) param;

        // this is an input argument, so we need to find it.

        handle = MethodHandles.insertArguments(handle, index, generateInputParameter(ip.name(), ip.type()));

      }
      else if (param instanceof OutputClassFieldArg.BeanParamArg)
      {

        BeanParamArg beanArg = (BeanParamArg) param;

        Object bean = createBean(beanArg, ctx, env);

        // we need to create this bean.

        handle = MethodHandles.insertArguments(handle, index, bean);

      }
      else if (param instanceof OutputClassFieldArg.ContextArg)
      {

        ContextArg carg = (ContextArg) param;

        if (carg.param().type().rawClass() == DataContext.class)
        {
          handle = MethodHandles.insertArguments(handle, index, ctx);
        }
        else
        {
          Object ctxval = env.getContext(carg.param());
          handle = MethodHandles.insertArguments(handle, index, ctxval);
        }
      }
      else
      {
        throw new RuntimeException();
      }

    }

    return handle;
  }

  private Object createBean(BeanParamArg beanArg, DataContext ctx, QueryEnvironment env)
  {

    List<Object> params = new ArrayList<>();

    for (Constructor<?> ctor : beanArg.param().type().rawClass().getConstructors())
    {

      ConstructorProperties aps = ctor.getAnnotation(ConstructorProperties.class);

      if (aps == null)
      {
        // no params, we wouldn't know how to create it.
        continue;
      }

      for (String field : aps.value())
      {

        TypedGetter<?> getter = beanArg.param().type().getter(field);

        if (getter == null)
        {
          throw new RuntimeException("unknown arg");
        }

        GQLArgument provided = ctx.selection().args(field);

        Object pval = generateInputParameter(field, getter.type());

        params.add(pval);

      }

      try
      {
        return ctor.newInstance(params.toArray());
      }
      catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
      {

        log.info("Error executing constructor {}", ctor);

        for (Object param : params)
        {
          log.info("Param: {}", param == null ? null : param.getClass().getSimpleName());
        }


        throw new RuntimeException("Error creating bean param", e);
      }

    }

    throw new RuntimeException("Unable to create param for " + beanArg.param().type());

  }

  /**
   * given a named input parameter, calculates the java object value for it.
   * 
   * @param name
   * @param type
   * @return
   */

  private Object generateInputParameter(String name, TypedClass<?> type)
  {

    GQLArgument arg = ctx.selection().args(name);

    if (arg == null)
    {
      // presumably, this is a nullable paramter. if it's not it's an error.
      return null;
    }

    // we need to convert into the input type method.

    GQLArgumentDefinition fdecl = ctx.fdecl().arg(arg.name());

    if (fdecl == null)
    {
      throw new RuntimeException("Can't find param for " + arg.name() + " in " + ctx.fdecl().args());
    }

    GQLValue value = arg.value() == null ? fdecl.defaultValue() : arg.value();

    if (value == null)
    {
      if (type.rawClass().isPrimitive())
      {
        throw new RuntimeException(String.format("Field needed value, but one wasn't provided."));
      }
      return null;
    }

    // this type could be a variable, input type, or scalar (and can be non-null and list).
    // we really need to do a double dispatch here, one to get the scalar type then one to
    // apply the value.
    return value.apply(new ToJavaValueVisitor(type, fdecl.type()));

  }

}
