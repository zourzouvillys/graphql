package io.joss.graphql.core.value;

public class GQLValueConverters
{

  public static final GQLValueVisitor<Long> longConverter()
  {

    return new DefaultValueVisitor<Long>() {

      @Override
      public Long visitDefaultValue(GQLValue value)
      {
        throw new IllegalArgumentException(value.getClass().toString());
      }

      @Override
      public Long visitStringValue(GQLStringValue value)
      {
        return Long.parseLong(value.value());
      }

      @Override
      public Long visitIntValue(GQLIntValue value)
      {
        return (long) value.value();
      }

      @Override
      public Long visitFloatValue(GQLFloatValue value)
      {
        return (long) value.value();
      }

      @Override
      public Long visitBooleanValue(GQLBooleanValue value)
      {
        return value == GQLBooleanValue.TRUE ? 1L : 0L;
      }

    };

  }

  public static final GQLValueVisitor<Integer> intConverter()
  {

    return new DefaultValueVisitor<Integer>() {

      @Override
      public Integer visitDefaultValue(GQLValue value)
      {
        throw new IllegalArgumentException(value.getClass().toString());
      }

      @Override
      public Integer visitStringValue(GQLStringValue value)
      {
        return Integer.parseInt(value.value());
      }

      @Override
      public Integer visitIntValue(GQLIntValue value)
      {
        return (int) value.value();
      }

      @Override
      public Integer visitFloatValue(GQLFloatValue value)
      {
        return (int) value.value();
      }

      @Override
      public Integer visitBooleanValue(GQLBooleanValue value)
      {
        return value == GQLBooleanValue.TRUE ? 1 : 0;
      }

    };

  }

  public static final GQLValueVisitor<Boolean> booleanConverter()
  {

    return new DefaultValueVisitor<Boolean>() {

      @Override
      public Boolean visitDefaultValue(GQLValue value)
      {
        throw new IllegalArgumentException(value.getClass().toString());
      }

      @Override
      public Boolean visitBooleanValue(GQLBooleanValue value)
      {
        return value == GQLBooleanValue.TRUE ? true : false;
      }

    };

  }

  public static final GQLValueVisitor<String> stringConverter()
  {

    return new DefaultValueVisitor<String>() {

      @Override
      public String visitDefaultValue(GQLValue value)
      {
        throw new IllegalArgumentException(value.getClass().toString());
      }

      @Override
      public String visitStringValue(GQLStringValue value)
      {
        return value.value();
      }

      @Override
      public String visitIntValue(GQLIntValue value)
      {
        return Long.toString(value.value());
      }

      @Override
      public String visitFloatValue(GQLFloatValue value)
      {
        return Double.toString(value.value());
      }

      @Override
      public String visitBooleanValue(GQLBooleanValue value)
      {
        return value == GQLBooleanValue.TRUE ? "true" : "false";
      }

      // @Override
      // public T visitVarValue(GQLVariableRef value)
      // {
      // return visitDefaultValue(value);
      // }

      @Override
      public String visitEnumValueRef(GQLEnumValueRef value)
      {
        return value.value();
      }

    };

  }

}
