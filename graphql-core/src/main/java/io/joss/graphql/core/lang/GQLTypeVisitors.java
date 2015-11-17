package io.joss.graphql.core.lang;

import io.joss.graphql.core.decl.GQLDeclaration;
import io.joss.graphql.core.types.GQLDeclarationRef;
import io.joss.graphql.core.types.GQLListType;
import io.joss.graphql.core.types.GQLNonNullType;
import lombok.experimental.UtilityClass;

@UtilityClass

public class GQLTypeVisitors
{

  public IsScalarTypeVisitor isScalar()
  {
    return new IsScalarTypeVisitor();
  }

  public IsListTypeVisitor isList()
  {
    return new IsListTypeVisitor();
  }

  /**
   * resolves concrete references. it will fail (on purpose) if the type is non-null or list.
   * 
   * @param typeRegistry
   * @return
   */

  public static TypeResolver resolver(GQLTypeRegistry typeRegistry)
  {
    return new TypeResolver(typeRegistry);
  }

  public static IsNonNullTypeVisitor isNotNull()
  {
    return new IsNonNullTypeVisitor();
  }

  /**
   * Fetches the root declaration type - if it's a list it's the inner type, if it's a non null, then it's the actual type
   * 
   * @return
   */

  public static GQLTypeVisitor<GQLDeclaration> rootType()
  {

    return new GQLTypeVisitor<GQLDeclaration>() {

      @Override
      public GQLDeclaration visitNonNull(GQLNonNullType type)
      {
        return type.type().apply(this);
      }

      @Override
      public GQLDeclaration visitList(GQLListType type)
      {
        return type.type().apply(this);
      }

      @Override
      public GQLDeclaration visitDeclarationRef(GQLDeclarationRef type)
      {
        return type;
      }

    };
  }

}
