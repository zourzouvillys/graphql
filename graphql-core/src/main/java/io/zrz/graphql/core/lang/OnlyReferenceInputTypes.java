package io.zrz.graphql.core.lang;

import io.zrz.graphql.core.decl.GQLTypeDeclaration;
import io.zrz.graphql.core.doc.GQLDefinitionVisitors;
import io.zrz.graphql.core.types.GQLDeclarationRef;
import io.zrz.graphql.core.types.GQLListType;
import io.zrz.graphql.core.types.GQLNonNullType;

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
