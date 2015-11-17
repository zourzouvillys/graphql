package io.joss.graphql.core.binder;

import io.joss.graphql.core.decl.GQLDeclaration;
import io.joss.graphql.core.lang.GQLTypeRegistry;

public class TypeBindingResult
{

  private GQLTypeRegistry registry;
  private TypeScanner scanner;
  private GQLDeclaration query;
  private GQLDeclaration mutation;

  TypeBindingResult(GQLTypeRegistry registry, TypeScanner scanner, GQLDeclaration query, GQLDeclaration mutation)
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

  public GQLDeclaration root()
  {
    return this.query;
  }

  public BindingProvider scanner()
  {
    return this.scanner;
  }

  public GQLDeclaration mutation()
  {
    return mutation;
  }

}
