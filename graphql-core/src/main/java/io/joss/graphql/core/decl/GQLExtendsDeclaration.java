package io.joss.graphql.core.decl;

/**
 * A declaration which extends an existing type.
 * 
 * @author theo
 *
 */

public class GQLExtendsDeclaration
{

  private GQLObjectTypeDeclaration type;

  public GQLExtendsDeclaration(GQLObjectTypeDeclaration type)
  {
    this.type = type;
  }

}
