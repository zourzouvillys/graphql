package io.zrz.graphql.core.binder.model;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import io.zrz.graphql.core.binder.annotatons.GQLArg;
import io.zrz.graphql.core.binder.annotatons.GQLBeanParams;
import io.zrz.graphql.core.binder.annotatons.GQLContext;
import io.zrz.graphql.core.binder.annotatons.GQLField;
import io.zrz.graphql.core.binder.execution.QueryEnvironment;
import io.zrz.graphql.core.binder.model.invoker.FieldInvoker;
import io.zrz.graphql.core.binder.model.invoker.FieldInvokerFactory;
import io.zrz.graphql.core.binder.reflect.ParameterizedTypedClass;
import io.zrz.graphql.core.binder.reflect.TypedArrayClass;
import io.zrz.graphql.core.binder.reflect.TypedClass;
import io.zrz.graphql.core.binder.reflect.TypedMethod;
import io.zrz.graphql.core.binder.reflect.TypedParameter;
import io.zrz.graphql.core.binder.runtime.DataContext;
import io.zrz.graphql.core.binder.runtime.InputObserver;
import io.zrz.graphql.core.binder.runtime.OutputObserver;
import io.zrz.graphql.core.decl.GQLParameterableFieldDeclaration;

public class OutputClassField
{

  private TypedMethod<?> method;

  private OutputClassField(TypedMethod<?> method)
  {

    this.method = method;

  }

  public String description()
  {
    if (method.getDeclaredAnnotation(GQLField.class) == null)
      return null;
    return method.getDeclaredAnnotation(GQLField.class).description();
  }

  public boolean isDeprecated()
  {
    return method.getDeclaredAnnotation(Deprecated.class) != null;
  }

  public String name()
  {

    if (method.getDeclaredAnnotation(GQLField.class) != null && !method.getDeclaredAnnotation(GQLField.class).name().isEmpty())
      return method.getDeclaredAnnotation(GQLField.class).name();

    
    String name = method.getName();

    if (name.startsWith("get") && Character.isUpperCase(name.charAt(3)))
    {
      return uncapitalize(name.substring(3));
    }

    if (returnType().rawClass().equals(Boolean.TYPE) || returnType().rawClass().equals(Boolean.class))
    {

      if (name.startsWith("is") && Character.isUpperCase(name.charAt(2)))
      {
        return uncapitalize(name.substring(2));
      }
      else if (name.startsWith("has") && Character.isUpperCase(name.charAt(3)))
      {
        return uncapitalize(name.substring(3));
      }

    }

    return method.getName();

  }

  private String uncapitalize(String string)
  {
    return Character.toLowerCase(string.charAt(0)) + (string.length() > 1 ? string.substring(1) : "");
  }

  /**
   * The expected input parameters. doesn't include bean ones.
   * 
   * @return
   */

  public List<OutputClassFieldArg.InputArg> inputParams()
  {

    return params().stream()
        .filter(a -> a instanceof OutputClassFieldArg.InputArg)
        .map(a -> OutputClassFieldArg.InputArg.class.cast(a))
        .collect(Collectors.toList());
  }

  /**
   * the expected input parameters, including those attached to bean parameters.
   * 
   * @return
   */

  public List<OutputClassFieldArg.ContextArg> contextParams()
  {
    return params().stream()
        .filter(a -> a instanceof OutputClassFieldArg.ContextArg)
        .map(a -> OutputClassFieldArg.ContextArg.class.cast(a))
        .collect(Collectors.toList());
  }

  public List<OutputClassFieldArg.BeanParamArg> beanParams()
  {
    return params().stream()
        .filter(a -> a instanceof OutputClassFieldArg.BeanParamArg)
        .map(a -> OutputClassFieldArg.BeanParamArg.class.cast(a))
        .collect(Collectors.toList());
  }

  /**
   * calculate each of the arguments.
   */

  public List<OutputClassFieldArg> params()
  {

    List<OutputClassFieldArg> args = new ArrayList<>();

    for (TypedParameter<?> param : method.params())
    {

      if (param.hasAnnotation(GQLContext.class))
      {
        args.add(OutputClassFieldArg.context(param));
      }
      else if (param.hasAnnotation(GQLBeanParams.class))
      {
        args.add(OutputClassFieldArg.beanParam(param));
      }
      else if (param.hasAnnotation(GQLArg.class))
      {
        args.add(OutputClassFieldArg.inputArg(param));
      }
      else
      {
        throw new RuntimeException("Expected one of: GQLBeanParams, GQLArg, or GQLContext for param " + param.index() + " on " + method.method().toString());
      }

    }

    return args;
  }

  /**
   * 
   * @return
   */

  public TypedClass<?> returnType()
  {

    if (isObserved())
    {

      if (!method.isStatic())
      {
        throw new RuntimeException("Observing method must be static and take the instance as the first value");
      }

      // if we're an observer type, we pretent to take an array.
      return new TypedArrayClass<>(
          ((ParameterizedTypedClass<?>) method.returnType()).parameter(0),
          new Annotation[0]);

    }

    return method.returnType();

  }

  private boolean isObserved()
  {
    return method.returnType().rawClass().equals(InputObserver.class);
  }

  public GQLParameterableFieldDeclaration decl()
  {
    return null;
  }

  public static OutputClassField bind(TypedMethod<?> method)
  {

    return new OutputClassField(method);
  }

  /**
   * Creates a new invoker for this method.
   * 
   * The invoker is then fed arguments using {@link InputObserver#onParent(Object, OutputObserver)}. When all the children have been fed to
   * it, {@link InputObserver#onCompleted()} must be called.
   * 
   * The method will use the provided output observer to return the value for each node.
   * 
   * It's important it's done like this to allow bulk processing of objects/fields. Without such a mechanism we couldn't provide a generic
   * framework which allows batching of SQL queries and generation of RPC in batches.
   * 
   * Note that execution engines can use this API in a variety of ways - from bulk scheduling, asynchronous parallel, or single thread. Or a
   * mixture based on annotations etc.
   * 
   */

  public FieldInvoker invoker(DataContext ctx, QueryEnvironment env)
  {
    if (ctx == null)
    {
      throw new IllegalArgumentException("ctx");
    }
    if (env == null)
    {
      throw new IllegalArgumentException("env");
    }
    return FieldInvokerFactory.bind(this, this.method, ctx, env);
  }

  @Override
  public String toString()
  {
    return String.format("OutputClassField(%s#%s)", this.method.method().getDeclaringClass().getName(), this.method.getName());
  }

}
