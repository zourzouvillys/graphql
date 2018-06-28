package io.zrz.graphql.zulu.doc;

import io.zrz.graphql.core.value.GQLBooleanValue;
import io.zrz.graphql.core.value.GQLEnumValueRef;
import io.zrz.graphql.core.value.GQLFloatValue;
import io.zrz.graphql.core.value.GQLIntValue;
import io.zrz.graphql.core.value.GQLListValue;
import io.zrz.graphql.core.value.GQLObjectValue;
import io.zrz.graphql.core.value.GQLStringValue;
import io.zrz.graphql.core.value.GQLValueVisitor;
import io.zrz.graphql.core.value.GQLVariableRef;

/**
 * if the value is a variable, provides the name - else null.
 *
 * @author theo
 *
 */

public class VariableNameExtractor implements GQLValueVisitor<String> {

  public VariableNameExtractor() {
  }

  @Override
  public String visitBooleanValue(final GQLBooleanValue value) {
    return null;
  }

  @Override
  public String visitIntValue(final GQLIntValue value) {
    return null;
  }

  @Override
  public String visitStringValue(final GQLStringValue value) {
    return null;
  }

  @Override
  public String visitFloatValue(final GQLFloatValue value) {
    return null;
  }

  // deferred

  @Override
  public String visitVarValue(final GQLVariableRef value) {
    return value.name();
  }

  @Override
  public String visitObjectValue(final GQLObjectValue value) {
    return null;
  }

  @Override
  public String visitListValue(final GQLListValue value) {
    // note: may have variables in the list ... ?
    return null;
  }

  @Override
  public String visitEnumValueRef(final GQLEnumValueRef value) {
    return null;
  }

}
