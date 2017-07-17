package io.joss.graphql.core.schema;

import io.joss.graphql.core.schema.model.EnumType;
import io.joss.graphql.core.schema.model.InputType;
import io.joss.graphql.core.schema.model.InterfaceType;
import io.joss.graphql.core.schema.model.ObjectType;
import io.joss.graphql.core.schema.model.ScalarType;
import io.joss.graphql.core.schema.model.UnionType;
import javax.annotation.Generated;

@Generated("io.zrz.visitors")
public final class TypeVisitors {
  @Generated("io.zrz.visitors")
  public interface GenericReturnVisitor<R> {
    R visitInputType(InputType value);

    R visitObjectType(ObjectType value);

    R visitEnumType(EnumType value);

    R visitInterfaceType(InterfaceType value);

    R visitScalarType(ScalarType value);

    R visitUnionType(UnionType value);
  }

  @Generated("io.zrz.visitors")
  public interface NoReturnVisitor {
    void visitInputType(InputType value);

    void visitObjectType(ObjectType value);

    void visitEnumType(EnumType value);

    void visitInterfaceType(InterfaceType value);

    void visitScalarType(ScalarType value);

    void visitUnionType(UnionType value);
  }
}
