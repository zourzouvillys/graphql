package io.joss.graphql.executor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.reflect.Invokable;
import com.google.common.reflect.Parameter;
import com.google.common.reflect.TypeToken;

import io.joss.graphql.core.binder.annotatons.GQLArg;
import io.joss.graphql.core.binder.annotatons.GQLContext;

/**
 * generic helper class which takes a method and provides methods for interacting semi-generically with it.
 * 
 * @author Theo Zourzouvillys
 *
 */

public class MethodBinder<T, R>
{

  public static interface ArgGenerator
  {
    Object generate(ParameterProvider provider);
  }

  private Map<String, Parameter> named = new HashMap<>();
  private Map<TypeToken<?>, Parameter> contextual = new HashMap<>();
  private List<Parameter> positional = Lists.newLinkedList();
  private List<ArgGenerator> args = Lists.newLinkedList();
  private Invokable<T, R> method;

  public MethodBinder(Invokable<T, R> from)
  {

    this.method = from;
    int positional = 0;

    for (Parameter p : from.getParameters())
    {
      if (p.getAnnotation(GQLContext.class) != null)
      {
        this.contextual.put(p.getType(), p);
        args.add((generator) -> generator.context(p));
      }
      else if (p.getAnnotation(GQLArg.class) != null)
      {
        final GQLArg arg = p.getAnnotation(GQLArg.class);
        named.put(arg.value(), p);
        args.add((generator) -> generator.named(arg.value(), p));
      }
      else
      {
        final int mpos = positional;
        this.positional.add(p);
        args.add((generator) -> generator.positional(mpos, p));
      }
    }

  }

  public static final <T> MethodBinder<T, Object> bind(Class<T> type, Method method)
  {
    return new MethodBinder<>(TypeToken.of(type).method(method));
  }

  public Map<String, Parameter> named()
  {
    return this.named;
  }

  public Map<TypeToken<?>, Parameter> contextual()
  {
    return this.contextual;
  }

  public List<Parameter> positional()
  {
    return this.positional;
  }

  /**
   * callbacks to fetch parameters.
   */

  public interface ParameterProvider
  {
    Object named(String name, Parameter p);

    Object context(Parameter p);

    Object positional(int position, Parameter p);
  }

  public class Invoker
  {

    public <T> Invoker bind(Class<T> context, T instance)
    {
      return this;
    }

    public Invoker named(String name, Object value)
    {
      return this;
    }

    public Invoker positional(Object value)
    {
      return this;
    }

    public Object invoke()
    {
      return null;
    }

  }

  public R invoke(T receiver, ParameterProvider provider)
  {
    try
    {
      return method.invoke(receiver, buildArgs(provider));
    }
    catch (InvocationTargetException | IllegalAccessException e)
    {
      Throwables.propagateIfPossible(e.getCause());
      throw Throwables.propagate(e);
    }
  }

  private Object[] buildArgs(ParameterProvider provider)
  {
    return this.args.stream().map(gen -> gen.generate(provider)).toArray();
  }

  public <R2 extends R> MethodBinder<T, R2> returning(Class<R2> klass)
  {
    return new MethodBinder<>(method.returning(klass));
  }

}
