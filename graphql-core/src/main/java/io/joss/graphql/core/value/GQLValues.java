package io.joss.graphql.core.value;

import java.util.List;
import java.util.Map;

import lombok.experimental.UtilityClass;

@UtilityClass

public class GQLValues
{

  /**
   * Returns a reference to the given variable.
   */

  public static GQLVariableRef variable(final String name)
  {
    return GQLVariableRef.builder().name(name).build();
  }

  /**
   * 
   * @param values
   * @return
   */

  public static GQLObjectValue objectValue(Map<String, GQLValue> values)
  {
    return GQLObjectValue.builder().values(values).build();
  }

  /**
   * The boolean false value.
   */

  public static GQLBooleanValue booleanFalse()
  {
    return GQLBooleanValue.FALSE;
  }

  /**
   * The boolean true value.
   */

  public static GQLBooleanValue booleanTrue()
  {
    return GQLBooleanValue.TRUE;
  }

  /**
   * Provides the boolean value representation of the java value.
   */

  public static GQLBooleanValue booleanValue(final boolean value)
  {
    return value ? GQLBooleanValue.TRUE : GQLBooleanValue.FALSE;
  }

  public static GQLIntValue intValue(final long value)
  {
    return GQLIntValue.from(value);
  }

  public static GQLStringValue stringValue(final String value)
  {
    return GQLStringValue.from(value);
  }

  /**
   * Note: the float value is actually a double in java (like an int is actually a ong). sorry for the confusion.
   *
   * @return
   */

  public static GQLValue floatValue(final double value)
  {
    return GQLFloatValue.from(value);
  }

  /**
   * an empty list.
   */

  public static GQLListValue listValue()
  {
    return GQLListValue.emptyListValue();
  }

  public static GQLValue listValue(List<GQLValue> values)
  {
    return GQLListValue.builder().values(values).build();
  }

  public static GQLValue listValue(GQLValue... values)
  {
    return GQLListValue.newValueList(values);
  }

  /**
   * An object of keys/values.
   */

  public static GQLObjectValue objectValue()
  {
    return GQLObjectValue.builder().build();
  }

  /**
   * A reference to an enum value.
   */

  public static GQLValue enumValueRef(final String value)
  {
    return GQLEnumValueRef.from(value);
  }

}
