package io.joss.graphql.core.value;

public abstract class DefaultValueVisitor<T> implements GQLValueVisitor<T>
{

  public abstract T visitDefaultValue(GQLValue value);

  @Override
  public T visitVarValue(GQLVariableRef value)
  {
    return visitDefaultValue(value);
  }

  @Override
  public T visitObjectValue(GQLObjectValue value)
  {
    return visitDefaultValue(value);
  }

  @Override
  public T visitListValue(GQLListValue value)
  {
    return visitDefaultValue(value);
  }

  @Override
  public T visitBooleanValue(GQLBooleanValue value)
  {
    return visitDefaultValue(value);
  }

  @Override
  public T visitIntValue(GQLIntValue value)
  {
    return visitDefaultValue(value);
  }

  @Override
  public T visitStringValue(GQLStringValue value)
  {
    return visitDefaultValue(value);
  }

  @Override
  public T visitFloatValue(GQLFloatValue value)
  {
    return visitDefaultValue(value);
  }

  @Override
  public T visitEnumValueRef(GQLEnumValueRef value)
  {
    return visitDefaultValue(value);
  }

}
