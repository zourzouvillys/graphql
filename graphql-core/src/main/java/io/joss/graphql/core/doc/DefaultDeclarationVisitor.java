package io.joss.graphql.core.doc;

import io.joss.graphql.core.decl.GQLDeclaration;
import io.joss.graphql.core.decl.GQLDeclarationVisitor;
import io.joss.graphql.core.decl.GQLEnumDeclaration;
import io.joss.graphql.core.decl.GQLInputTypeDeclaration;
import io.joss.graphql.core.decl.GQLInterfaceTypeDeclaration;
import io.joss.graphql.core.decl.GQLObjectTypeDeclaration;
import io.joss.graphql.core.decl.GQLScalarTypeDeclaration;
import io.joss.graphql.core.decl.GQLUnionTypeDeclaration;

public class DefaultDeclarationVisitor<T> implements GQLDeclarationVisitor<T>
{

  private T defaultValue;

  public DefaultDeclarationVisitor(T defaultValue)
  {
    this.defaultValue = defaultValue;
  }
  
  protected T visitDefault(GQLDeclaration type)
  {
    return defaultValue;
  }

  @Override
  public T visitUnion(GQLUnionTypeDeclaration type)
  {
    return visitDefault(type);
  }

  @Override
  public T visitScalar(GQLScalarTypeDeclaration type)
  {
    return visitDefault(type);
  }

  @Override
  public T visitObject(GQLObjectTypeDeclaration type)
  {
    return visitDefault(type);
  }

  @Override
  public T visitInterface(GQLInterfaceTypeDeclaration type)
  {
    return visitDefault(type);
  }

  @Override
  public T visitEnum(GQLEnumDeclaration type)
  {
    return visitDefault(type);
  }

  @Override
  public T visitInput(GQLInputTypeDeclaration type)
  {
    return visitDefault(type);
  }

}
