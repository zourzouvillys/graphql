package io.joss.graphql.core.lang;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import io.joss.graphql.core.decl.GQLDeclaration;

/**
 * Provides utilities for building a schema. This involves resolving type references, peforming type validations, etc.
 *
 * @author theo
 *
 */

public class GQLSchemaBuilder
{

  private final Map<String, GQLDeclaration> types = new HashMap<>();

  public GQLSchemaBuilder add(final GQLDeclaration type)
  {
    
    if (type.name() == null || type.name().isEmpty())
    {
      throw new IllegalArgumentException("invalid name");
    }
    else if (types.containsKey(type.name()))
    {
      throw new DuplicateTypeNameException(type.name());
    }
    
    type.apply(new ValidateDeclarationVisitor());
    this.types.put(type.name(), type);
    
    return this;
    
  }

  public GQLSchemaBuilder add(final Collection<GQLDeclaration> type)
  {
    type.forEach(t -> add(t));
    return this;
  }

  /**
   *
   * @return
   */

  public GQLTypeRegistry build()
  {
    
    final Map<String, GQLDeclaration> types = new HashMap<>();

    for (Map.Entry<String, GQLDeclaration> e : this.types.entrySet())
    {
      types.put(e.getKey(), e.getValue().apply(new ResolveReferencesVisitor(this.types)));      
    }
    
    
    GQLTypeRegistry reg = new GQLTypeRegistry(types);
    
    reg.apply(new PostCreationValidator(reg));

    return reg;

  }

}
