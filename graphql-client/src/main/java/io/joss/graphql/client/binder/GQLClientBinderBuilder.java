package io.joss.graphql.client.binder;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import io.joss.graphql.client.runtime.GQLChannel;
import io.joss.graphql.client.runtime.GQLPath;
import io.joss.graphql.client.runtime.RuntimeQuery;
import io.joss.graphql.core.binder.reflect.ParameterizedTypedClass;
import io.joss.graphql.core.binder.reflect.ReflectionUtils;
import io.joss.graphql.core.binder.reflect.TypedClass;
import io.joss.graphql.core.binder.reflect.TypedMethod;
import io.joss.graphql.core.doc.GQLFieldSelection;
import io.joss.graphql.core.doc.GQLOperationDefinition;
import io.joss.graphql.core.doc.GQLSelection;

/**
 * Binds a class by creating an implementation.
 * 
 * @author theo
 *
 */

public class GQLClientBinderBuilder<T>
{

  private final Class<T> stub;
  private GQLChannel channel;

  public GQLClientBinderBuilder(Class<T> stub)
  {
    this.stub = stub;
  }

  public GQLClientBinderBuilder<T> withChannel(GQLChannel channel)
  {
    this.channel = channel;
    return this;
  }

  public static <T> GQLClientBinderBuilder<T> forStub(Class<T> stub)
  {
    if (!stub.isInterface())
    {
      throw new IllegalArgumentException("stub must be an interface");
    }
    return new GQLClientBinderBuilder<>(stub);
  }

  /**
   * build an instance, that is bound to the client.
   * 
   * The bound interface will be scanned for methods, each one will map to a single GQL query.
   * 
   * The returned type of the method will map to class instances which contain paths through the use of {@link GQLPath}.
   * 
   */

  @SuppressWarnings("unchecked")
  public T build()
  {

    if (this.channel == null)
    {
      throw new IllegalStateException("channel is required");
    }

    TypedClass<Object> stub = ReflectionUtils.forClass(this.stub);

    BoundClient client = new BoundClient(this.channel);

    for (TypedMethod<?> method : stub.getDeclaredMethods())
    {

      if (method.returnType().rawClass().equals(RuntimeQuery.class))
      {
        List<GQLSelection> sel = generateQuery(((ParameterizedTypedClass<?>) method.returnType()).parameter(0).rawClass());
        GQLOperationDefinition op = GQLOperationDefinition.builder().selections(sel).build();
        System.err.println(op);
        client.add(method.method(), op);
      }
      else
      {
        throw new RuntimeException("Don't know how to bind to method " + method);
      }

    }

    return (T) Proxy.newProxyInstance(
        getClass().getClassLoader(),
        new Class[] { this.stub },
        (proxy, method, args) -> client.dispatch(proxy, method, args));

  }

  /**
   * fetches the selections based on the field names and the annotations on the client stubs.
   */

  private List<GQLSelection> generateQuery(Class<?> klass)
  {

    List<GQLSelection> selection = new LinkedList<>();

    for (Method m : klass.getDeclaredMethods())
    {

      if (!Modifier.isPublic(m.getModifiers()))
      {
        continue;
      }
      else if (isObjectOverride(m))
      {
        continue;
      }

      if (m.getReturnType().equals(Collection.class))
      {

        GQLFieldSelection.Builder fsb = GQLFieldSelection.builder();

        fsb.name(m.getName());

        Class<?> rtype = (Class<?>) ((ParameterizedType) m.getGenericReturnType()).getActualTypeArguments()[0];

        if (!rtype.isPrimitive() && !rtype.equals(String.class))
        {
          fsb.selections(generateQuery(rtype));
        }

        selection.add(fsb.build());

      }
      else 
      {

        GQLFieldSelection.Builder fsb = GQLFieldSelection.builder();

        fsb.name(m.getName());

        if (!m.getReturnType().isPrimitive() && !m.getReturnType().equals(String.class))
        {
          fsb.selections(generateQuery(m.getReturnType()));
        }

        selection.add(fsb.build());

      }

    }

    return selection;

  }

  private boolean isObjectOverride(Method m)
  {

    if (m.getDeclaringClass().equals(Object.class))
    {
      return true;
    }

    for (Method om : Object.class.getDeclaredMethods())
    {
      if (om.getName().equals(m.getName()))
        return true;
    }

    return false;

    // m.isAnnotationPresent(Override.class) && Object.class.hasMethod(m.getName(), m.getParameterTypes()) != null

    // TODO Auto-generated method stub
  }

}
