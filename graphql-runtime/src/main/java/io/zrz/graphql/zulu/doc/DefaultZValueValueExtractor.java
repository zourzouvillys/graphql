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
import io.zrz.zulu.types.ZField;
import io.zrz.zulu.values.ZValue;
import io.zrz.zulu.values.ZValues;

/**
 * provides a default value for an argument if none is provided at runtime.
 *
 * @author theo
 *
 */

public class DefaultZValueValueExtractor implements GQLValueVisitor<Optional<ZValue>> {

  private final DefaultGQLPreparedOperation req;
  private final GQLArgument arg;

  public DefaultZValueValueExtractor(final DefaultGQLPreparedOperation req, final GQLArgument arg) {
    this.req = req;
    this.arg = arg;
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
    final Optional<? extends ZField> field = this.req.inputType().field(value.name());
    return field.flatMap(x -> Optional.ofNullable(x.constantValue().orElse(x.defaultValue().orElse(null))));
  }

  @Override
  public Optional<ZValue> visitObjectValue(final GQLObjectValue value) {
    throw new RuntimeException("not implemented");
  }

  @Override
  public Optional<ZValue> visitListValue(final GQLListValue value) {
    return LocalZValues.toList(value);
  }

  @Override
  public Optional<ZValue> visitEnumValueRef(final GQLEnumValueRef value) {
    throw new RuntimeException("not implemented");
  }

}
