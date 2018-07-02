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

  private final DefaultGQLPreparedOperation req;
  private final GQLArgument arg;
  private final GQLVariableProvider provider;

  public ConstantZValueValueExtractor(final DefaultGQLPreparedOperation req, final GQLArgument arg, final GQLVariableProvider provider) {
    this.req = req;
    this.arg = arg;
    this.provider = provider;
  }

  @Override
  public Optional<ZValue> visitBooleanValue(final GQLBooleanValue value) {
    return Optional.of(ZValues.of(value.value()));
  }

  @Override
  public Optional<ZValue> visitIntValue(final GQLIntValue value) {
    return Optional.of(ZValues.of(value.value()));
  }

  @Override
  public Optional<ZValue> visitStringValue(final GQLStringValue value) {
    return Optional.of(ZValues.of(value.value()));
  }

  @Override
  public Optional<ZValue> visitFloatValue(final GQLFloatValue value) {
    return Optional.of(ZValues.of(value.value()));
  }

  // deferred

  @Override
  public Optional<ZValue> visitVarValue(final GQLVariableRef value) {

    if (this.provider == null) {
      return Optional.empty();
    }

    return this.provider.resolve(value);

  }

  /**
   * an input object has been passed in, defined in
   */

  @Override
  public Optional<ZValue> visitObjectValue(final GQLObjectValue value) {
    throw new RuntimeException("not implemented");
  }

  /**
   *
   */

  @Override
  public Optional<ZValue> visitListValue(final GQLListValue value) {
    return LocalZValues.toList(value);
  }

  @Override
  public Optional<ZValue> visitEnumValueRef(final GQLEnumValueRef value) {
    return Optional.of(ZValues.of(value.value()));
  }

}
