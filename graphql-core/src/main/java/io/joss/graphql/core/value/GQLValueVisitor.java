package io.joss.graphql.core.value;

public interface GQLValueVisitor<R>
{

  /**
   * a reference to a named variable.
   */

  R visitVarValue(GQLVariableRef value);

  /**
   * A (possible untyped) set of key/values.
   */

  R visitObjectValue(GQLObjectValue value);

  /**
   * A list value.
   */

  R visitListValue(GQLListValue value);

  /**
   * A literal boolean value.
   */

  R visitBooleanValue(GQLBooleanValue value);

  /**
   * An literal integer value. which is actually a long.
   */

  R visitIntValue(GQLIntValue value);

  /**
   * A literal string value.
   */

  R visitStringValue(GQLStringValue value);

  /**
   * A literal float value.
   */

  R visitFloatValue(GQLFloatValue value);

  /**
   * An enum value (or at least, we hope it is - it's not been resolved yet).
   */

  R visitEnumValueRef(GQLEnumValueRef value);

}
