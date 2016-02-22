package io.joss.graphql.schema;

import java.util.Collection;

import io.joss.graphql.core.binder.annotatons.GQLField;
import io.joss.graphql.core.binder.annotatons.GQLType;

@GQLType(name = "__Schema")
public class __Schema
{

  private __Type queryRoot;
  private Collection<__Type> types;

  public __Schema(__Type queryRoot, Collection<__Type> types)
  {
    this.queryRoot = queryRoot;
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
    return null;
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
