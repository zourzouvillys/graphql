package io.joss.graphql.core.lang;

import io.joss.graphql.core.decl.GQLArgumentDefinition;
import io.joss.graphql.core.decl.GQLDeclarationVisitor;
import io.joss.graphql.core.decl.GQLEnumDeclaration;
import io.joss.graphql.core.decl.GQLInputTypeDeclaration;
import io.joss.graphql.core.decl.GQLInterfaceTypeDeclaration;
import io.joss.graphql.core.decl.GQLObjectTypeDeclaration;
import io.joss.graphql.core.decl.GQLParameterableFieldDeclaration;
import io.joss.graphql.core.decl.GQLScalarTypeDeclaration;
import io.joss.graphql.core.decl.GQLUnionTypeDeclaration;

public class ValidateDeclarationVisitor implements GQLDeclarationVisitor<Void>
{

  @Override
  public Void visitUnion(GQLUnionTypeDeclaration type)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Void visitScalar(GQLScalarTypeDeclaration type)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Void visitObject(GQLObjectTypeDeclaration type)
  {
    type.fields().forEach(this::visitFieldDeclaration);
    return null;
  }
  
  /**
   * ensure that all 
   * @param type
   * @return
   */

  @Override
  public Void visitInput(GQLInputTypeDeclaration type)
  {
    // TODO Auto-generated method stub
    return null;
  }

  private void visitFieldDeclaration(GQLParameterableFieldDeclaration type)
  {
    if (type.type() == null)
    {
      throw new IllegalArgumentException(type.toString());
    }

    type.args().forEach(this::validateArgumentDefinition);

  }

  private void validateArgumentDefinition(GQLArgumentDefinition def)
  {

    if (def.type() == null)
    {
      throw new IllegalArgumentException();
    }

  }

  @Override
  public Void visitInterface(GQLInterfaceTypeDeclaration type)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Void visitEnum(GQLEnumDeclaration type)
  {
    // TODO Auto-generated method stub
    return null;
  }

}
