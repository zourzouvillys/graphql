package io.joss.graphql.core.binder.execution;

import java.util.HashMap;
import java.util.Map;

import io.joss.graphql.core.binder.reflect.TypedParameter;
import io.joss.graphql.executor.GraphQLOutputType;
import lombok.Builder;
import lombok.Singular;

@Builder
public class QueryEnvironment
{

  private static final QueryEnvironment EMPTY_INSTANCE = QueryEnvironment.builder().build();

  @Singular
  private Map<Class<?>, Object> contexts = new HashMap<>();

  public Object getContext(TypedParameter<?> param)
  {
    return contexts.get(param.type().rawClass());
  }

  public Object getContext(Class<?> param)
  {
    return contexts.get(param);
  }

  public static QueryEnvironment emptyEnvironment()
  {
    return EMPTY_INSTANCE;
  }

}
