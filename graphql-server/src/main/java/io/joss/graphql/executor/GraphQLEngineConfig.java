package io.joss.graphql.executor;

import java.util.Map;
import java.util.Set;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import io.joss.graphql.core.binder.annotatons.GQLType;

public class GraphQLEngineConfig
{

  /**
   * the types that are registered in this config.
   * 
   * note that once we're created, this becomes immutable.
   * 
   */

  private Set<GraphQLOutputType> types = Sets.newHashSet();
  private Map<Class<?>, GraphQLOutputType> klasses = Maps.newHashMap();
  private Map<String, GraphQLOutputType> named = Maps.newHashMap();

  private GraphQLOutputType root;

  /**
   * Register the specified instance to have it's fields added to the query root.
   */

  public void registerQuery(Object instance)
  {
    registerType(GraphQLOutputType.builder(instance.getClass()).build());
  }

  /**
   * Register a specific type.
   */

  public GraphQLOutputType registerType(GraphQLOutputType type)
  {
    this.types.add(type);
    this.named.put(type.name(), type);
    return type;
  }

  /**
   * 
   */

  public GraphQLOutputType registerType(Class<?> type)
  {
    return registerType(GraphQLOutputType.builder(type).build());
  }

  /**
   * 
   */

  public void queryRoot(GraphQLOutputType root)
  {
    this.root = root;
  }

  /**
   * 
   */

  public GraphQLOutputType queryRoot()
  {
    Preconditions.checkNotNull(this.root);
    return this.root;
  }

  /**
   * 
   */

  public GraphQLOutputType type(Class<?> type)
  {

    GQLType gt = type.getAnnotation(GQLType.class);

    if (gt == null)
    {
      return null;
    }

    if (gt.name().isEmpty())
    {
      return type(type.getSimpleName());
    }

    return type(gt.name());
  }

  private GraphQLOutputType type(String name)
  {
    return this.named.get(name);
  }

}
