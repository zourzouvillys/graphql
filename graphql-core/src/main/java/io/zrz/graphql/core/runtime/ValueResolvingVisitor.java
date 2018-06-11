package io.zrz.graphql.core.runtime;

import java.util.Objects;

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

  private DefaultGQLPreparedOperation req;

  public ValueResolvingVisitor(DefaultGQLPreparedOperation req) {
    this.req = req;
  }

  /**
   * provides late-binding of values.
   * 
   * @param req
   * @param value
   * @return
   */

  public static ZValueProvider create(DefaultGQLPreparedOperation req, GQLValue value) {
    return Objects.requireNonNull(value.apply(new ValueResolvingVisitor(req)), value.toString());
  }

  /**
   * 
   */

  @Override
  public ZValueProvider visitBooleanValue(GQLBooleanValue arg0) {
    return StaticZValueProvider.of(arg0.value());
  }

  @Override
  public ZValueProvider visitEnumValueRef(GQLEnumValueRef arg0) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ZValueProvider visitFloatValue(GQLFloatValue arg0) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ZValueProvider visitIntValue(GQLIntValue arg0) {
    return StaticZValueProvider.of(arg0.value());
  }

  @Override
  public ZValueProvider visitListValue(GQLListValue arg0) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ZValueProvider visitObjectValue(GQLObjectValue arg0) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ZValueProvider visitStringValue(GQLStringValue arg0) {
    // TODO Auto-generated method stub
    return StaticZValueProvider.of(arg0.value());
  }

  @Override
  public ZValueProvider visitVarValue(GQLVariableRef arg0) {
    return VarRefValueProvider.ofVar(req.typeResolver(), arg0,
        this.req.operation().vars().stream().filter(x -> x.name().equals(arg0.name())).findFirst().get());
  }

}
