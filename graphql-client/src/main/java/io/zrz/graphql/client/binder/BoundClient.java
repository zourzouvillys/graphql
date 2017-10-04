package io.zrz.graphql.client.binder;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import io.zrz.graphql.client.runtime.GQLChannel;
import io.zrz.graphql.client.runtime.RuntimeQuery;
import io.zrz.graphql.core.binder.reflect.ParameterizedTypedClass;
import io.zrz.graphql.core.binder.reflect.ReflectionUtils;
import io.zrz.graphql.core.binder.reflect.TypedClass;
import io.zrz.graphql.core.converter.TypeConverter;
import io.zrz.graphql.core.doc.GQLDocument;
import io.zrz.graphql.core.doc.GQLOperationDefinition;
import io.zrz.graphql.core.value.GQLObjectValue;
import io.zrz.graphql.core.value.GQLValue;
import io.zrz.graphql.core.value.GQLValueTypeConverter;

/**
 * A reflection based implementation of a bound client. A faster implementation could certainly be implemented using pre-compilation, but
 * this should do the job for now.
 * 
 * @author theo
 *
 */

public class BoundClient
{

  private Map<Method, GQLOperationDefinition> methods = new HashMap<>();
  private GQLChannel channel;
  private TypeConverter converter = new GQLValueTypeConverter();

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

    TypedClass<Object> returnType = ReflectionUtils.wrap(method.getAnnotatedReturnType());

    GQLOperationDefinition op = methods.get(method);

    GQLDocument doc = GQLDocument.builder()
        .definition(op)
        .build();

    if (returnType.rawClass().isInstance(RuntimeQuery.class))
    {
      throw new RuntimeException("Client stub should return a RuntimeQuery<T>");
    }

    // the type we are returning to the caller.
    TypedClass<?> type = ((ParameterizedTypedClass<?>) returnType).parameter(0);

    return new RuntimeQuery<Object>() {

      @Override
      public Object execute()
      {

        // perform the fetch, which returns the GQL value result.
        GQLObjectValue result = channel.execute(doc).get();
        
        // use the converter to perform it.
        return converter.convert(result, type.getType());
        
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
    Converter c = new Converter(converter, result, type);
    return Proxy.newProxyInstance(
        getClass().getClassLoader(),
        new Class<?>[] { type.rawClass() },
        (proxy, method, args) -> c.dispatch(proxy, method, args));
  }

}
