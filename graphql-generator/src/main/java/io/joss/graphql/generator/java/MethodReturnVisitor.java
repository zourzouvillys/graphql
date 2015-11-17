package io.joss.graphql.generator.java;

import io.joss.graphql.core.decl.GQLDeclaration;
import io.joss.graphql.core.decl.GQLDeclarationVisitor;
import io.joss.graphql.core.decl.GQLScalarTypeDeclaration;
import io.joss.graphql.core.doc.DefaultDeclarationVisitor;
import io.joss.graphql.core.lang.GQLTypeVisitor;
import io.joss.graphql.core.types.GQLDeclarationRef;
import io.joss.graphql.core.types.GQLListType;
import io.joss.graphql.core.types.GQLNonNullType;

public class MethodReturnVisitor extends DefaultDeclarationVisitor<String> implements GQLTypeVisitor<String>
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
    return type.apply((GQLDeclarationVisitor<String>) this);
  }

  protected String visitDefault(GQLDeclaration type)
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
