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

public class VariableNameExtractor implements GQLValueVisitor<String> {

  public VariableNameExtractor() {
  }

  @Override
  public String visitBooleanValue(final GQLBooleanValue value) {
    throw new IllegalArgumentException();
  }

  @Override
  public String visitIntValue(final GQLIntValue value) {
    throw new IllegalArgumentException();
  }

  @Override
  public String visitStringValue(final GQLStringValue value) {
    throw new IllegalArgumentException();
  }

  @Override
  public String visitFloatValue(final GQLFloatValue value) {
    throw new IllegalArgumentException();
  }

  // deferred

  @Override
  public String visitVarValue(final GQLVariableRef value) {
    return value.name();
  }

  @Override
  public String visitObjectValue(final GQLObjectValue value) {
    throw new RuntimeException("not implemented");
  }

  @Override
  public String visitListValue(final GQLListValue value) {
    throw new RuntimeException("not implemented");
  }

  @Override
  public String visitEnumValueRef(final GQLEnumValueRef value) {
    throw new RuntimeException("not implemented");
  }

}
