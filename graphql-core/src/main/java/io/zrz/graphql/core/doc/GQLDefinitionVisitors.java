package io.zrz.graphql.core.doc;

import io.zrz.graphql.core.decl.GQLEnumDeclaration;
import io.zrz.graphql.core.decl.GQLInputTypeDeclaration;
import io.zrz.graphql.core.decl.GQLScalarTypeDeclaration;
import io.zrz.graphql.core.decl.GQLTypeDeclarationVisitor;
import io.zrz.graphql.core.utils.DefaultTypeDeclarationVisitor;

public class GQLDefinitionVisitors {

  public static GQLDefinitionVisitor<GQLOperationDefinition> operationExtractor() {
    return new GQLDefinitionVisitor<GQLOperationDefinition>() {

      @Override
      public GQLOperationDefinition visitOperation(GQLOperationDefinition op) {
        return op;
      }

      @Override
      public GQLOperationDefinition visitFragment(GQLFragmentDefinition frag) {
        return null;
      }

    };
  }

  public static GQLDefinitionVisitor<GQLFragmentDefinition> fragmentExtractor() {
    return new GQLDefinitionVisitor<GQLFragmentDefinition>() {

      @Override
      public GQLFragmentDefinition visitOperation(GQLOperationDefinition op) {
        return null;
      }

      @Override
      public GQLFragmentDefinition visitFragment(GQLFragmentDefinition frag) {
        return frag;
      }

    };
  }

  /**
   * A visitor which returns true for input or scalar declarations, false for everythign else.
   * 
   * @return
   */

  public static GQLTypeDeclarationVisitor<Boolean> isInputOrScalarVisitor() {

    return new DefaultTypeDeclarationVisitor<Boolean>(false) {

      @Override
      public Boolean visitScalar(GQLScalarTypeDeclaration type) {
        return true;
      }

      @Override
      public Boolean visitInput(GQLInputTypeDeclaration type) {
        return true;
      }

    };
  }

  /**
   * true if this type is readable (e.g, it's an INPUT, SCALAR, or ENUM).
   * 
   * @return
   */

  public static GQLTypeDeclarationVisitor<Boolean> isReadableType() {

    return new DefaultTypeDeclarationVisitor<Boolean>(false) {

      @Override
      public Boolean visitScalar(GQLScalarTypeDeclaration type) {
        return true;
      }

      @Override
      public Boolean visitInput(GQLInputTypeDeclaration type) {
        return true;
      }

      @Override
      public Boolean visitEnum(GQLEnumDeclaration type) {
        return true;
      }

    };
  }

}
