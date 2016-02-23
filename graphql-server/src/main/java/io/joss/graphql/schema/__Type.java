package io.joss.graphql.schema;

import com.google.common.base.Preconditions;

import io.joss.graphql.core.binder.annotatons.GQLContext;
import io.joss.graphql.core.binder.annotatons.GQLField;
import io.joss.graphql.core.binder.annotatons.GQLType;
import io.joss.graphql.core.lang.GQLTypeVisitor;
import io.joss.graphql.core.types.GQLDeclarationRef;
import io.joss.graphql.core.types.GQLListType;
import io.joss.graphql.core.types.GQLNonNullType;
import io.joss.graphql.core.types.GQLTypeReference;
import io.joss.graphql.executor.GraphQLEngine;
import io.joss.graphql.executor.GraphQLInputType;
import io.joss.graphql.executor.GraphQLOutputType;

@GQLType(name = "__Type")
public class __Type
{

  private GraphQLOutputType type;
  private GQLNonNullType nonNull;
  private String scalar;
  private GQLListType list;
  private GraphQLInputType input;

  public __Type(GraphQLOutputType type)
  {
    this.type = Preconditions.checkNotNull(type);
  }

  public __Type(GraphQLInputType type)
  {
    this.input = Preconditions.checkNotNull(type);
  }

  public __Type(GQLNonNullType type)
  {
    this.nonNull = Preconditions.checkNotNull(type);
  }

  public __Type(String scalar)
  {
    this.scalar = scalar;
  }

  public __Type(GQLListType list)
  {
    this.list = list;
  }

  @GQLField
  public String kind()
  {
    if (this.nonNull != null)
    {
      return "NON_NULL";
    }
    else if (this.type != null)
    {
      return "OBJECT";
    }
    else if (this.scalar != null)
    {
      return "SCALAR";
    }
    else if (this.list != null)
    {
      return "LIST";
    }
    else if (this.input != null)
    {
      return "INPUT_OBJECT";
    }
    return null;
  }

  @GQLField
  public String name()
  {
    if (this.nonNull != null)
    {
      return nonNull.toString();
    }
    else if (this.type != null)
    {
      return this.type.name();
    }
    else if (this.scalar != null)
    {
      return this.scalar;
    }
    else if (this.list != null)
    {
      return null;
    }
    else if (this.input != null)
    {
      return this.input.name();
    }
    return null;
  }

  @GQLField
  public String description()
  {
    return null;
  }

  @GQLField
  public __Field[] fields()
  {
    if (type != null)
    {
      return type.fields().stream().map(__Field::new).filter(t -> !t.name().startsWith("__")).toArray(__Field[]::new);
    }
    return null;
  }

  @GQLField
  public __Type[] interfaces()
  {
    return new __Type[0];
  }

  @GQLField
  public __InputValue[] inputFields()
  {
    if (input != null)
    {
      return input.fields().stream().map(__InputValue::new).filter(t -> !t.name().startsWith("__")).toArray(__InputValue[]::new);
    }
    return new __InputValue[0];
  }

  @GQLField
  public String[] enumValues()
  {
    return new String[0];
  }

  @GQLField
  public __Type[] possibleTypes()
  {
    return new __Type[0];
  }

  @GQLField
  public __Type ofType(@GQLContext GraphQLEngine engine)
  {
    if (this.nonNull != null)
    {
      return type(engine, nonNull.type());
    }
    else if (this.type != null)
    {
      return null;
    }
    else if (this.scalar != null)
    {
      return null;
    }
    else if (this.list != null)
    {
      return type(engine, this.list.type());
    }
    return null;
  }

  public static __Type type(GraphQLEngine engine, GQLTypeReference type)
  {

    Preconditions.checkNotNull(type);

    return type.apply(new GQLTypeVisitor<__Type>() {

      @Override
      public __Type visitNonNull(GQLNonNullType type)
      {
        return new __Type(type);
      }

      @Override
      public __Type visitList(GQLListType type)
      {
        return new __Type(type);
      }

      @Override
      public __Type visitDeclarationRef(GQLDeclarationRef type)
      {

        GraphQLOutputType xtype = engine.type(type.name());

        if (xtype == null)
        {

          GraphQLInputType itype = engine.inputType(type.name());

          if (itype != null)
          {
            return new __Type(itype);
          }

          // must be a scalar...
          return new __Type(type.name());
          // throw new RuntimeException(String.format("Unable to resolve type '%s'", type.name()));
        }

        return new __Type(xtype);

      }

    });

  }

}
