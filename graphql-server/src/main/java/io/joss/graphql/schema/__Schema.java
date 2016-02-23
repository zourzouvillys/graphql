package io.joss.graphql.schema;

import java.util.Collection;

import io.joss.graphql.core.binder.annotatons.GQLField;
import io.joss.graphql.core.binder.annotatons.GQLType;

@GQLType(name = "__Schema")
public class __Schema
{

  private __Type queryRoot;
  private Collection<__Type> types;
  private __Type mutationRoot;

  public __Schema(__Type queryRoot, __Type mutationRoot, Collection<__Type> types)
  {
    this.queryRoot = queryRoot;
    this.mutationRoot = mutationRoot;
    this.types = types;
  }

  @GQLField
  public __Type[] types()
  {
    return types.toArray(new __Type[0]);
  }

  @GQLField
  public __Type queryType()
  {
    return queryRoot;
  }

  @GQLField
  public __Type mutationType()
  {
    return mutationRoot;
  }

  @GQLField
  public __Type subscriptionType()
  {
    return null;
  }

  @GQLField
  public __Directive[] directives()
  {
    return new __Directive[0];
  }

  // @GQLField
  // public Collection<Directive> directives()
  // {
  // return Collections.emptyList();
  // }

}
