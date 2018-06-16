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

public class ConstantZValueValueExtractor implements GQLValueVisitor<Optional<ZValue>> {

  private DefaultGQLPreparedOperation req;
  private GQLArgument arg;
  private GQLVariableProvider provider;

  public ConstantZValueValueExtractor(DefaultGQLPreparedOperation req, GQLArgument arg, GQLVariableProvider provider) {
    this.req = req;
    this.arg = arg;
    this.provider = provider;
  }

  @Override
  public Optional<ZValue> visitBooleanValue(GQLBooleanValue value) {
    return Optional.of(ZValues.of(value.value()));
  }

  @Override
  public Optional<ZValue> visitIntValue(GQLIntValue value) {
    return Optional.of(ZValues.of(value.value()));
  }

  @Override
  public Optional<ZValue> visitStringValue(GQLStringValue value) {
    return Optional.of(ZValues.of(value.value()));
  }

  @Override
  public Optional<ZValue> visitFloatValue(GQLFloatValue value) {
    return Optional.of(ZValues.of(value.value()));
  }

  // deferred

  @Override
  public Optional<ZValue> visitVarValue(GQLVariableRef value) {

    if (this.provider == null) {
      return Optional.empty();
    }

    return this.provider.resolve(value);

  }

  @Override
  public Optional<ZValue> visitObjectValue(GQLObjectValue value) {
    throw new RuntimeException("not implemented");
  }

  @Override
  public Optional<ZValue> visitListValue(GQLListValue value) {
    throw new RuntimeException("not implemented");
  }

  @Override
  public Optional<ZValue> visitEnumValueRef(GQLEnumValueRef value) {
    throw new RuntimeException("not implemented");
  }

}
