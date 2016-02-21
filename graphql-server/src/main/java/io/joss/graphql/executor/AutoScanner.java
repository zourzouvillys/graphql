package io.joss.graphql.executor;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import io.joss.graphql.core.binder.annotatons.GQLArg;
import io.joss.graphql.core.binder.annotatons.GQLContext;
import io.joss.graphql.core.binder.annotatons.GQLDefaultValue;
import io.joss.graphql.core.binder.annotatons.GQLField;
import io.joss.graphql.core.doc.GQLArgument;
import io.joss.graphql.core.doc.GQLSelection;
import io.joss.graphql.core.value.GQLValueConverters;
import io.joss.graphql.core.value.GQLValueTypeConverter;
import io.joss.graphql.core.value.GQLVariableRef;
import io.joss.graphql.executor.GraphQLOutputType.ArgBuilder;
import io.joss.graphql.executor.GraphQLOutputType.Builder;
import io.joss.graphql.executor.GraphQLOutputType.Field;
import io.joss.graphql.executor.GraphQLOutputType.FieldBuilder;
import lombok.extern.slf4j.Slf4j;

/**
 * Utils for generating the input types out of reflection.
 * 
 * @author Theo Zourzouvillys
 *
 */

@Slf4j
final class AutoScanner
{

  static void scan(GraphQLOutputType.Builder b, Class<?> klass)
  {

    // GQLType type

    b.name(klass.getSimpleName());

    // keeps track of the actual method names, to see when we're already overridden.
    Set<String> matched = new HashSet<>();

    while (klass != null)
    {

      for (Method method : klass.getDeclaredMethods())
      {

        if (matched.contains(method.getName()))
        {
          // we've already matched this method named with an @GQLField.
          continue;
        }

        if (method.getAnnotation(GQLField.class) == null)
        {
          // only GQLField annotated methods get one ...
          continue;
        }

        matched.add(method.getName());

        method(b, method);

      }

      klass = klass.getSuperclass();

    }

  }

  public static interface ArgGenerator
  {

    Object generate(ExecutionContext ctx, List<GQLArgument> args, Object[] roots);

  }

  public abstract static class AbstractFieldHandler implements FieldHandler
  {

    protected final Method method;
    private List<ArgGenerator> args = Lists.newLinkedList();

    public AbstractFieldHandler(Method method)
    {

      this.method = method;

      for (int i = 0; i < method.getParameterCount(); ++i)
      {

        Parameter p = method.getParameters()[i];
        Class<?> type = method.getParameterTypes()[i];

        if (p.getAnnotation(GQLContext.class) != null)
        {
          args.add((ctx, args, roots) -> Preconditions.checkNotNull(ctx.env().getContext(type), type.getName()));
        }
        else if (p.getAnnotation(GQLArg.class) != null)
        {
          // the parametrer is an input.
          GQLArg arg = p.getAnnotation(GQLArg.class);
          args.add((ctx, args, roots) -> arg(args, arg, type));
        }
        else if (Modifier.isStatic(method.getModifiers()))
        {
          args.add((ctx, args, roots) -> roots);
        }
        else
        {
          System.err.println(p);
          throw new RuntimeException("Unknown Parameter on " + method);
        }

      }

    }

    private Object arg(List<GQLArgument> args, GQLArg ant, Class<?> type)
    {

      for (GQLArgument arg : args)
      {
        if (arg.name().equals(ant.value()))
        {
          // note that we resolve variables BEFORE we dispatch, as it only needs to be done once per request rather than each node.
          return GQLValueTypeConverter.getInstance().convert(arg.value(), type);
        }
      }
      
      // ??

      return null;
    }

    abstract Class<?> returnType();

    /**
     * @param arg
     * @param ctx
     * @param ctx
     * 
     */

    private Object[] generateParameters(ExecutionContext ctx, List<GQLArgument> arg, Object[] roots)
    {

      Object[] ret = new Object[args.size()];

      for (int i = 0; i < args.size(); ++i)
      {
        ret[i] = args.get(i).generate(ctx, arg, roots);
      }

      return ret;

    }

  }

  /**
   * Handler which processes static fields (that can do bulk).
   * 
   * @author theo
   *
   */

  private static final class StaticFieldHandler extends AbstractFieldHandler implements FieldHandler
  {

    public StaticFieldHandler(Method method)
    {
      super(method);
    }

    @Override
    public Object[] value(Object[] roots, GraphQLOutputType type, Field field, ExecutionContext ctx, List<GQLArgument> args, List<GQLSelection> selection)
    {

      try
      {
        // note: breaks if returning primitive[] rather than boxed[].
        return (Object[]) method.invoke(null, super.generateParameters(ctx, args, roots));
      }
      catch (Throwable t)
      {
        // always wrap.
        throw new RuntimeException(t);
      }

    }

    /**
     * The java type returned.
     */

    @Override
    Class<?> returnType()
    {
      return method.getReturnType().getComponentType();
    }

  }

  private static final class DynamicFieldHandler extends AbstractFieldHandler implements FieldHandler
  {

    public DynamicFieldHandler(Method method)
    {
      super(method);
    }

    /**
     * The java type returned.
     */

    Class<?> returnType()
    {
      return method.getReturnType();
    }

    /**
     * Perform selection.
     */

    @Override
    public Object[] value(Object[] roots, GraphQLOutputType type, Field field, ExecutionContext ctx, List<GQLArgument> args, List<GQLSelection> selection)
    {

      Preconditions.checkNotNull(method);

      Object[] results = (Object[]) Array.newInstance(box(method.getReturnType()), roots.length);

      for (int i = 0; i < roots.length; ++i)
      {

        if (roots[i] == null)
        {
          continue;
        }

        try
        {
          results[i] = method.invoke(roots[i], super.generateParameters(ctx, args, new Object[] { roots[i] }));
        }
        catch (Throwable t)
        {
          log.warn("{} {}", method, roots);
          t.printStackTrace();
        }

      }

      return results;

    }

  }

  // add the method.

  static void method(Builder b, Method method)
  {

    GQLField field = method.getAnnotation(GQLField.class);

    FieldBuilder fb = b.addField(Strings.isNullOrEmpty(field.name()) ? method.getName() : field.name());

    for (int i = 0; i < method.getParameterCount(); ++i)
    {
      param(fb, method.getParameters()[i]);
    }

    AbstractFieldHandler handler;

    if (Modifier.isStatic(method.getModifiers()))
    {
      handler = new StaticFieldHandler(method);
      // use the static model. one call for all paths. effecient!
    }
    else
    {
      handler = new DynamicFieldHandler(method);
      // use the dynamic (instance based) model.
    }

    // also select the GQL type for returning.
    // fb.type(GQLTypes.concreteTypeRef("xxx"));

    fb.handler(handler);

  }

  // we need to box for now. sigh.

  private static Class<?> box(Class<?> returnType)
  {
    Preconditions.checkNotNull(returnType);
    if (returnType.equals(Integer.TYPE))
    {
      return Integer.class;
    }
    return returnType;
  }

  private static void param(FieldBuilder fb, Parameter p)
  {

    GQLArg arg = p.getAnnotation(GQLArg.class);

    if (arg == null)
    {
      return;
    }

    ArgBuilder ab = fb.newArg(arg.value());

    GQLDefaultValue val = p.getAnnotation(GQLDefaultValue.class);

    if (val != null)
    {
      ab.defaultValue(val.value());
    }

    // calculate the GQLType?

  }

}
