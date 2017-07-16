package io.joss.graphql.core.binder.runtime;

import io.joss.graphql.core.decl.GQLTypeDeclarationVisitor;
import io.joss.graphql.core.decl.GQLEnumDeclaration;
import io.joss.graphql.core.decl.GQLInputTypeDeclaration;
import io.joss.graphql.core.decl.GQLInterfaceTypeDeclaration;
import io.joss.graphql.core.decl.GQLObjectTypeDeclaration;
import io.joss.graphql.core.decl.GQLParameterableFieldDeclaration;
import io.joss.graphql.core.decl.GQLScalarTypeDeclaration;
import io.joss.graphql.core.decl.GQLUnionTypeDeclaration;
import io.joss.graphql.core.doc.GQLFieldSelection;
import io.joss.graphql.core.lang.GQLTypeRegistry;
import io.joss.graphql.core.lang.GQLTypeVisitor;
import io.joss.graphql.core.types.GQLDeclarationRef;
import io.joss.graphql.core.types.GQLListType;
import io.joss.graphql.core.types.GQLNonNullType;
import io.joss.graphql.core.types.GQLTypes;

/**
 * Extracts a named field.
 * 
 * @author theo
 *
 */

public class FieldExtractor implements GQLTypeVisitor<GQLParameterableFieldDeclaration>, GQLTypeDeclarationVisitor<GQLParameterableFieldDeclaration>
{

  private GQLFieldSelection field;
  private GQLTypeRegistry reg;

  public FieldExtractor(GQLTypeRegistry reg, GQLFieldSelection field)
  {
    this.field = field;
    this.reg = reg;
  }

  @Override
  public GQLParameterableFieldDeclaration visitUnion(GQLUnionTypeDeclaration type)
  {
    // TODO Auto-generated method stub
    throw new RuntimeException("Not implemented");
  }

  @Override
  public GQLParameterableFieldDeclaration visitScalar(GQLScalarTypeDeclaration type)
  {
    // TODO Auto-generated method stub
    throw new RuntimeException("Not implemented");
  }

  @Override
  public GQLParameterableFieldDeclaration visitObject(GQLObjectTypeDeclaration type)
  {

    if (field.name().equals("__typename"))
    {
      return GQLParameterableFieldDeclaration.builder().name("__typename").type(GQLTypes.stringType()).build();
    }

    GQLParameterableFieldDeclaration ret = type.field(field.name());

    if (ret != null)
    {
      return ret;
    }

    // try the interfaces?

    for (GQLDeclarationRef ref : type.ifaces())
    {
      ret = this.reg.resolve(ref).apply(this);
      if (ret != null)
      {
        return ret;
      }
    }

    return null;

  }

  @Override
  public GQLParameterableFieldDeclaration visitNonNull(GQLNonNullType type)
  {
    return type.type().apply(this);
  }

  @Override
  public GQLParameterableFieldDeclaration visitList(GQLListType type)
  {
    return type.type().apply(this);
  }

  @Override
  public GQLParameterableFieldDeclaration visitInterface(GQLInterfaceTypeDeclaration type)
  {

    if (field.name().equals("__typename"))
    {
      return GQLParameterableFieldDeclaration.builder().name("__typename").type(GQLTypes.stringType()).build();
    }

    GQLParameterableFieldDeclaration ret = type.field(field.name());

    if (ret != null)
    {
      return ret;
    }

    for (GQLDeclarationRef ref : type.ifaces())
    {
      ret = this.reg.resolve(ref).apply(this);
      if (ret != null)
      {
        return ret;
      }
    }

    return null;
  }

  @Override
  public GQLParameterableFieldDeclaration visitEnum(GQLEnumDeclaration type)
  {
    // TODO Auto-generated method stub
    throw new RuntimeException("Not implemented");
  }

  @Override
  public GQLParameterableFieldDeclaration visitDeclarationRef(GQLDeclarationRef type)
  {
    return reg.resolve(type).apply(this);
  }

  @Override
  public GQLParameterableFieldDeclaration visitInput(GQLInputTypeDeclaration type)
  {
    // TODO Auto-generated method stub
    throw new RuntimeException("Not implemented");
  }

}
