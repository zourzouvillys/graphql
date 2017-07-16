package io.joss.graphql.core.lang;

import io.joss.graphql.core.decl.GQLTypeDeclaration;
import io.joss.graphql.core.doc.GQLDefinitionVisitors;
import io.joss.graphql.core.types.GQLDeclarationRef;
import io.joss.graphql.core.types.GQLListType;
import io.joss.graphql.core.types.GQLNonNullType;

public class OnlyReferenceInputTypes implements GQLTypeVisitor<Boolean>
{

  private GQLTypeRegistry reg;

  public OnlyReferenceInputTypes(GQLTypeRegistry reg)
  {
    this.reg = reg;
  }

  @Override
  public Boolean visitNonNull(GQLNonNullType type)
  {
    return type.type().apply(this);
  }

  @Override
  public Boolean visitList(GQLListType type)
  {
    return type.type().apply(this);
  }

  @Override
  public Boolean visitDeclarationRef(GQLDeclarationRef ref)
  {
    GQLTypeDeclaration type = this.reg.resolve(ref);
    return type.apply(GQLDefinitionVisitors.isInputOrScalarVisitor());
  }

}
