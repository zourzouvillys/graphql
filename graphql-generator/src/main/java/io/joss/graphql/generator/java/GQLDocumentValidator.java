package io.joss.graphql.generator.java;

import io.joss.graphql.core.doc.GQLDocument;
import io.joss.graphql.core.doc.GQLFragmentDefinition;
import io.joss.graphql.core.lang.GQLTypeRegistry;

public class GQLDocumentValidator
{

  private GQLTypeRegistry registry;

  public GQLDocumentValidator(GQLTypeRegistry registry)
  {
    this.registry = registry;
  }

  /**
   * performs some higher level document validation.
   * 
   * @param document
   */

  public void validate(GQLDocument document)
  {

    for (GQLFragmentDefinition frag : document.fragments())
    {

      if (registry.resolve(frag.namedType()) == null)
      {
        throw new IllegalArgumentException(
            String.format("Fragment '%s' refers to unknown type '%s'", frag.name(), frag.namedType().name()));
      }

    }

    // TODO: check that any references to fragment variables are included in the outer scope.
    
  }

}
