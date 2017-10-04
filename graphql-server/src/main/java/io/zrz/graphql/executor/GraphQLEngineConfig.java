package io.zrz.graphql.executor;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import io.zrz.graphql.core.binder.annotatons.GQLType;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
  private Map<String, GraphQLInputType> inputTypesByName = Maps.newHashMap();
  private GraphQLOutputType root;
  private GraphQLOutputType mutationRoot;

  private Set<GraphQLInputType> inputTypes = Sets.newHashSet();

  /**
   * Register the specified instance to have it's fields added to the query root.
   */

  public void registerQuery(Object instance)
  {
    registerType(GraphQLOutputType.builder(instance.getClass()).build());
  }

  public GraphQLInputType registerType(GraphQLInputType type)
  {
    log.trace("Registering GQL input type {}", type.name());
    this.inputTypes.add(type);
    this.inputTypesByName.put(type.name(), type);
    return type;
  }

  /**
   * Register a specific type.
   */

  public GraphQLOutputType registerType(GraphQLOutputType type)
  {
    this.types.add(type);
    log.trace("Registering GQL output type {}", type.name());
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

  public void mutationRoot(GraphQLOutputType root)
  {
    this.mutationRoot = root;
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

  public GraphQLOutputType type(String name)
  {
    return this.named.get(name);
  }

  public Collection<GraphQLOutputType> types()
  {
    return this.types;
  }

  public Collection<GraphQLInputType> inputTypes()
  {
    return this.inputTypes;
  }

  public GraphQLOutputType mutationRoot()
  {
    return this.mutationRoot;
  }

  public GraphQLInputType inputType(String name)
  {
    return this.inputTypesByName.get(name);
  }

}
