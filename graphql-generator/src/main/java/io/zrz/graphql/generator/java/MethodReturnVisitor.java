package io.zrz.graphql.generator.java;

import io.zrz.graphql.core.decl.GQLScalarTypeDeclaration;
import io.zrz.graphql.core.decl.GQLTypeDeclaration;
import io.zrz.graphql.core.decl.GQLTypeDeclarationVisitor;
import io.zrz.graphql.core.lang.GQLTypeVisitor;
import io.zrz.graphql.core.types.GQLDeclarationRef;
import io.zrz.graphql.core.types.GQLListType;
import io.zrz.graphql.core.types.GQLNonNullType;
import io.zrz.graphql.core.utils.DefaultTypeDeclarationVisitor;

public class MethodReturnVisitor extends DefaultTypeDeclarationVisitor<String> implements GQLTypeVisitor<String>
{

  private JavaClientGenerator gen;

  public MethodReturnVisitor(JavaClientGenerator gen)
  {
    super(null);
    this.gen = gen;
  }

  @Override
  public String visitNonNull(GQLNonNullType type)
  {
    String inner = type.type().apply(this);
    switch (inner)
    {
      case "int":
      case "long":
      case "float":
      case "boolean":
        return inner;
    }
    return String.format("@GQLNonNull %s", type.type().apply(this));
  }

  @Override
  public String visitList(GQLListType type)
  {
    return String.format("Collection<%s>", type.type().apply(this));
  }

  @Override
  public String visitDeclarationRef(GQLDeclarationRef type)
  {
    return type.apply((GQLTypeDeclarationVisitor<String>) this);
  }

  protected String visitDefault(GQLTypeDeclaration type)
  {
    return type.name();
  }

  @Override
  public String visitScalar(GQLScalarTypeDeclaration type)
  {
    if (type.name().equals("Int"))
    {
      return "int";
    }
    return visitDefault(type);
  }
}
