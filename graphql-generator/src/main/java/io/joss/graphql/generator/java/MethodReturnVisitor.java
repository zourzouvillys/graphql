package io.joss.graphql.generator.java;

import io.joss.graphql.core.decl.GQLTypeDeclaration;
import io.joss.graphql.core.decl.GQLTypeDeclarationVisitor;
import io.joss.graphql.core.decl.GQLScalarTypeDeclaration;
import io.joss.graphql.core.lang.GQLTypeVisitor;
import io.joss.graphql.core.types.GQLDeclarationRef;
import io.joss.graphql.core.types.GQLListType;
import io.joss.graphql.core.types.GQLNonNullType;
import io.joss.graphql.core.utils.DefaultTypeDeclarationVisitor;

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
