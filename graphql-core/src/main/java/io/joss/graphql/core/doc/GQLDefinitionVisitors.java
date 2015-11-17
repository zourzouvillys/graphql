package io.joss.graphql.core.doc;

import io.joss.graphql.core.decl.GQLDeclarationVisitor;
import io.joss.graphql.core.decl.GQLInputTypeDeclaration;
import io.joss.graphql.core.decl.GQLScalarTypeDeclaration;

public class GQLDefinitionVisitors
{

  public static GQLDefinitionVisitor<GQLOperationDefinition> operationExtractor()
  {
    return new GQLDefinitionVisitor<GQLOperationDefinition>() {

      @Override
      public GQLOperationDefinition visitOperation(GQLOperationDefinition op)
      {
        return op;
      }

      @Override
      public GQLOperationDefinition visitFragment(GQLFragmentDefinition frag)
      {
        return null;
      }

    };
  }

  public static GQLDefinitionVisitor<GQLFragmentDefinition> fragmentExtractor()
  {
    return new GQLDefinitionVisitor<GQLFragmentDefinition>() {

      @Override
      public GQLFragmentDefinition visitOperation(GQLOperationDefinition op)
      {
        return null;
      }

      @Override
      public GQLFragmentDefinition visitFragment(GQLFragmentDefinition frag)
      {
        return frag;
      }

    };
  }

  /**
   * A visitor which returns true for input or scalar declarations, false for everythign else.
   * 
   * @return
   */

  public static GQLDeclarationVisitor<Boolean> isInputOrScalarVisitor()
  {

    return new DefaultDeclarationVisitor<Boolean>(false) {

      @Override
      public Boolean visitScalar(GQLScalarTypeDeclaration type)
      {
        return true;
      }

      @Override
      public Boolean visitInput(GQLInputTypeDeclaration type)
      {
        return true;
      }

    };
  }

}
