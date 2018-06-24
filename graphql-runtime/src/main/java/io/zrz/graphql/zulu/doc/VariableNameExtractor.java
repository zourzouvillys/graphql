package io.zrz.graphql.zulu.doc;

import java.util.Optional;

import io.zrz.graphql.core.doc.GQLArgument;
import io.zrz.graphql.core.value.GQLBooleanValue;
import io.zrz.graphql.core.value.GQLEnumValueRef;
import io.zrz.graphql.core.value.GQLFloatValue;
import io.zrz.graphql.core.value.GQLIntValue;
import io.zrz.graphql.core.value.GQLListValue;
import io.zrz.graphql.core.value.GQLObjectValue;
import io.zrz.graphql.core.value.GQLStringValue;
import io.zrz.graphql.core.value.GQLValueVisitor;
import io.zrz.graphql.core.value.GQLVariableRef;
import io.zrz.zulu.values.ZValue;
import io.zrz.zulu.values.ZValues;

public class VariableNameExtractor implements GQLValueVisitor<String> {

  private DefaultGQLPreparedOperation req;
  private GQLArgument arg;
  private GQLVariableProvider provider;

  public VariableNameExtractor(DefaultGQLPreparedOperation req, GQLArgument arg) {
    this.req = req;
    this.arg = arg;
    this.provider = provider;
  }

  @Override
  public String visitBooleanValue(GQLBooleanValue value) {
    throw new IllegalArgumentException();
  }

  @Override
  public String visitIntValue(GQLIntValue value) {
    throw new IllegalArgumentException();
  }

  @Override
  public String visitStringValue(GQLStringValue value) {
    throw new IllegalArgumentException();
  }

  @Override
  public String visitFloatValue(GQLFloatValue value) {
    throw new IllegalArgumentException();
  }

  // deferred

  @Override
  public String visitVarValue(GQLVariableRef value) {
    return value.name();
  }

  @Override
  public String visitObjectValue(GQLObjectValue value) {
    throw new RuntimeException("not implemented");
  }

  @Override
  public String visitListValue(GQLListValue value) {
    throw new RuntimeException("not implemented");
  }

  @Override
  public String visitEnumValueRef(GQLEnumValueRef value) {
    throw new RuntimeException("not implemented");
  }

}
