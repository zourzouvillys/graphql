package io.joss.graphql.client.binder;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import io.joss.graphql.client.runtime.GQLChannel;
import io.joss.graphql.client.runtime.RuntimeQuery;
import io.joss.graphql.core.binder.reflect.ParameterizedTypedClass;
import io.joss.graphql.core.binder.reflect.ReflectionUtils;
import io.joss.graphql.core.binder.reflect.TypedClass;
import io.joss.graphql.core.binder.reflect.TypedParameter;
import io.joss.graphql.core.doc.GQLDocument;
import io.joss.graphql.core.doc.GQLOperationDefinition;
import io.joss.graphql.core.value.GQLObjectValue;

/**
 * A slow reflection based implementation.
 * 
 * @author theo
 *
 */

public class BoundClient
{

  private Map<Method, GQLOperationDefinition> methods = new HashMap<>();
  private GQLChannel channel;

  public BoundClient(GQLChannel channel)
  {
    this.channel = channel;
  }

  void add(Method method, GQLOperationDefinition op)
  {
    this.methods.put(method, op);
  }

  public Object dispatch(Object proxy, Method method, Object[] args)
  {

    GQLOperationDefinition op = methods.get(method);

    GQLDocument doc = GQLDocument.builder()
        .definition(op)
        .build();

    return new RuntimeQuery<Object>() {

      @Override
      public Object execute()
      {
        TypedClass<Object> returnType = ReflectionUtils.wrap(method.getAnnotatedReturnType());
        
        return convert(channel.execute(doc).get(), ((ParameterizedTypedClass<?>)returnType).parameter(0));
      }

    };

  }

  /**
   * converts the given result to the specified type.
   * 
   * @param result
   * @param type
   * 
   * @return
   */

  private Object convert(GQLObjectValue result, TypedClass<?> type)
  {
    Converter c = new Converter(result, type);
    return Proxy.newProxyInstance(
        getClass().getClassLoader(),
        new Class<?>[] { type.rawClass() },
        (proxy, method, args) -> c.dispatch(proxy, method, args));
  }

}
