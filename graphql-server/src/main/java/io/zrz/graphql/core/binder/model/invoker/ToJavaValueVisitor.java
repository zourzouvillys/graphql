package io.zrz.graphql.core.binder.model.invoker;

import java.util.Collection;
import java.util.stream.Collectors;

import io.zrz.graphql.core.binder.reflect.TypedClass;
import io.zrz.graphql.core.types.GQLTypeReference;
import io.zrz.graphql.core.value.GQLBooleanValue;
import io.zrz.graphql.core.value.GQLEnumValueRef;
import io.zrz.graphql.core.value.GQLFloatValue;
import io.zrz.graphql.core.value.GQLIntValue;
import io.zrz.graphql.core.value.GQLListValue;
import io.zrz.graphql.core.value.GQLObjectValue;
import io.zrz.graphql.core.value.GQLStringValue;
import io.zrz.graphql.core.value.GQLValue;
import io.zrz.graphql.core.value.GQLValueVisitor;
import io.zrz.graphql.core.value.GQLVariableRef;

public class ToJavaValueVisitor implements GQLValueVisitor<Object>
{

  private TypedClass<?> targetType;

  public ToJavaValueVisitor(TypedClass<?> targetType, GQLTypeReference gqltype)
  {
    this.targetType = targetType;
  }

  /**
   * fetch the value, then re-apply.
   */

  @Override
  public Object visitVarValue(GQLVariableRef value)
  {
    throw new RuntimeException("not supported");
  }

  @Override
  public Object visitObjectValue(GQLObjectValue value)
  {
    throw new RuntimeException("not supported");
  }

  @Override
  public Object visitListValue(GQLListValue values)
  {
    if (Collection.class.isAssignableFrom(targetType.rawClass()))
    {
      return values.values().stream()
          .map(val -> val.apply(new ToJavaValueVisitor(targetType.asCollection().componentType(), null)))
          .collect(Collectors.toList());
    }
    else
    {
      throw new RuntimeException(String.format("Primitive arrays on input types not yet supported.  Please use a collection instead."));
    }
    
  }

  @Override
  public Object visitBooleanValue(GQLBooleanValue value)
  {
    return value == GQLBooleanValue.TRUE;
  }

  @Override
  public Object visitIntValue(GQLIntValue value)
  {
    if (targetType.rawClass().equals(Long.TYPE))
    {
      return (long) value.value();
    }
    return (int) value.value();
  }

  @Override
  public Object visitStringValue(GQLStringValue value)
  {
    if (targetType.rawClass().equals(Long.TYPE) || targetType.rawClass().equals(Long.class))
    {
      return Long.parseLong(value.value());
    }
    if (targetType.rawClass().equals(Integer.TYPE) || targetType.rawClass().equals(Integer.class))
    {
      return Integer.parseInt(value.value());
    }
    if (targetType.rawClass().equals(Boolean.TYPE) || targetType.rawClass().equals(Boolean.class))
    {
      return Boolean.parseBoolean(value.value());
    }
    return value.value();
  }

  @Override
  public Object visitFloatValue(GQLFloatValue value)
  {
    throw new RuntimeException("not supported");
  }

  @Override
  public Object visitEnumValueRef(GQLEnumValueRef value)
  {
    throw new RuntimeException("not supported");
  }

}
