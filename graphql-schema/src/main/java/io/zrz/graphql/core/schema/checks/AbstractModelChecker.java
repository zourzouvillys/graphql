package io.zrz.graphql.core.schema.checks;

import io.zrz.graphql.core.schema.TypeVisitors;
import io.zrz.graphql.core.schema.model.EnumType;
import io.zrz.graphql.core.schema.model.InputType;
import io.zrz.graphql.core.schema.model.InterfaceType;
import io.zrz.graphql.core.schema.model.ObjectType;
import io.zrz.graphql.core.schema.model.ScalarType;
import io.zrz.graphql.core.schema.model.UnionType;

public class AbstractModelChecker implements TypeVisitors.NoReturnVisitor {

  @Override
  public void visitInputType(InputType value) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visitObjectType(ObjectType value) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visitEnumType(EnumType value) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visitInterfaceType(InterfaceType value) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visitScalarType(ScalarType value) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visitUnionType(UnionType value) {
    // TODO Auto-generated method stub

  }

}
