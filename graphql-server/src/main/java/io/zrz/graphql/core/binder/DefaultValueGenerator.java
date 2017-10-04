package io.zrz.graphql.core.binder;

import io.zrz.graphql.core.lang.GQLTypeVisitor;
import io.zrz.graphql.core.types.GQLDeclarationRef;
import io.zrz.graphql.core.types.GQLListType;
import io.zrz.graphql.core.types.GQLNonNullType;
import io.zrz.graphql.core.types.GQLTypeReference;
import io.zrz.graphql.core.value.GQLStringValue;
import io.zrz.graphql.core.value.GQLValue;
import io.zrz.graphql.core.value.GQLValues;

public class DefaultValueGenerator implements GQLTypeVisitor<GQLValue>
{

  private GQLTypeReference type;
  private String value;

  public DefaultValueGenerator(GQLTypeReference type, String value)
  {
    this.type = type;
    this.value = value;
  }

  @Override
  public GQLValue visitNonNull(GQLNonNullType type)
  {
    return type.type().apply(this);
  }

  @Override
  public GQLValue visitList(GQLListType type)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public GQLValue visitDeclarationRef(GQLDeclarationRef type)
  {

    switch (type.name())
    {
      case "String":
        return new GQLStringValue(value);
      case "Int":
        return GQLValues.intValue(Long.parseLong(value));
      case "Float":
        return GQLValues.floatValue(Double.parseDouble(value));
      case "Boolean":
        return GQLValues.booleanValue(Boolean.parseBoolean(value));
    }

    throw new RuntimeException("No idea how to convert string to " + type.name());

  }

}
