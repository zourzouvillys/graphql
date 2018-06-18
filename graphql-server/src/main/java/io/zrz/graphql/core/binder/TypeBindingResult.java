package io.zrz.graphql.core.binder;

import io.zrz.graphql.core.decl.GQLObjectTypeDeclaration;
import io.zrz.graphql.core.decl.GQLTypeDeclaration;
import io.zrz.graphql.core.lang.GQLTypeRegistry;

public class TypeBindingResult
{

  private GQLTypeRegistry registry;
  private TypeScanner scanner;
  private GQLTypeDeclaration query;
  private GQLObjectTypeDeclaration mutation;

  public TypeBindingResult(GQLTypeRegistry registry, TypeScanner scanner, GQLObjectTypeDeclaration query, GQLObjectTypeDeclaration mutation)
  {
    this.registry = registry;
    this.scanner = scanner;
    this.query = query;
    this.mutation = mutation;
  }

  public GQLTypeRegistry registry()
  {
    return this.registry;
  }

  public GQLTypeDeclaration root()
  {
    return this.query;
  }

  public BindingProvider scanner()
  {
    return this.scanner;
  }

  public GQLObjectTypeDeclaration mutation()
  {
    return mutation;
  }

}