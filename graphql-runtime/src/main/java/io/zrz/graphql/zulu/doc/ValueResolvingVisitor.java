package io.zrz.graphql.zulu.doc;

import io.zrz.graphql.core.doc.GQLVariableDefinition;
import io.zrz.graphql.core.value.GQLBooleanValue;
import io.zrz.graphql.core.value.GQLEnumValueRef;
import io.zrz.graphql.core.value.GQLFloatValue;
import io.zrz.graphql.core.value.GQLIntValue;
import io.zrz.graphql.core.value.GQLListValue;
import io.zrz.graphql.core.value.GQLObjectValue;
import io.zrz.graphql.core.value.GQLStringValue;
import io.zrz.graphql.core.value.GQLValue;
import io.zrz.graphql.core.value.GQLValueVisitor;
import io.zrz.graphql.core.value.GQLVariableRef;
import io.zrz.zulu.values.StaticZValueProvider;
import io.zrz.zulu.values.ZValueProvider;

class ValueResolvingVisitor implements GQLValueVisitor<ZValueProvider> {

  private final DefaultGQLPreparedOperation req;

  public ValueResolvingVisitor(final DefaultGQLPreparedOperation req) {
    this.req = req;
  }

  /**
   * provides late-binding of values.
   *
   * @param req
   * @param value
   * @return
   */

  public static ZValueProvider create(final DefaultGQLPreparedOperation req, final GQLValue value) {
    final ZValueProvider provider = value.apply(new ValueResolvingVisitor(req));
    if (provider == null)
      return null;
    return provider;
  }

  /**
   *
   */

  @Override
  public ZValueProvider visitBooleanValue(final GQLBooleanValue arg0) {
    return StaticZValueProvider.of(arg0.value());
  }

  @Override
  public ZValueProvider visitEnumValueRef(final GQLEnumValueRef arg0) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ZValueProvider visitFloatValue(final GQLFloatValue arg0) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ZValueProvider visitIntValue(final GQLIntValue arg0) {
    return StaticZValueProvider.of(arg0.value());
  }

  @Override
  public ZValueProvider visitListValue(final GQLListValue arg0) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ZValueProvider visitObjectValue(final GQLObjectValue arg0) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ZValueProvider visitStringValue(final GQLStringValue arg0) {
    // TODO Auto-generated method stub
    return StaticZValueProvider.of(arg0.value());
  }

  @Override
  public ZValueProvider visitVarValue(final GQLVariableRef arg0) {
    final GQLVariableDefinition def = this.req
        .operation()
        .vars()
        .stream()
        .filter(x -> x.name().equals(arg0.name()))
        .findFirst()
        .orElse(null);
    if (def == null)
      return null;
    return VarRefValueProvider.ofVar(
        this.req.typeResolver(),
        arg0,
        def);
  }

}
