package io.zrz.graphql.core.schema;

import java.lang.Override;
import javax.annotation.Generated;

import io.zrz.graphql.core.schema.model.EnumType;
import io.zrz.graphql.core.schema.model.InputType;
import io.zrz.graphql.core.schema.model.InterfaceType;
import io.zrz.graphql.core.schema.model.ObjectType;
import io.zrz.graphql.core.schema.model.ScalarType;
import io.zrz.graphql.core.schema.model.Type;
import io.zrz.graphql.core.schema.model.UnionType;

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
  public abstract static class DefaultAbstractGenericReturnVisitor<R> implements GenericReturnVisitor<R> {
    protected abstract R visitDefault(Type value);

    @Override
    public R visitInputType(InputType value) {
      return visitDefault(value);
    }

    @Override
    public R visitObjectType(ObjectType value) {
      return visitDefault(value);
    }

    @Override
    public R visitEnumType(EnumType value) {
      return visitDefault(value);
    }

    @Override
    public R visitInterfaceType(InterfaceType value) {
      return visitDefault(value);
    }

    @Override
    public R visitScalarType(ScalarType value) {
      return visitDefault(value);
    }

    @Override
    public R visitUnionType(UnionType value) {
      return visitDefault(value);
    }
  }

  @Generated("io.zrz.visitors")
  public static class DefaultGenericReturnVisitor<R> extends DefaultAbstractGenericReturnVisitor<R> implements GenericReturnVisitor<R> {
    protected final R _defaultValue;

    public DefaultGenericReturnVisitor(R defaultValue) {
      this._defaultValue = defaultValue;
    }

    public DefaultGenericReturnVisitor() {
      this._defaultValue = null;
    }

    protected R visitDefault(Type value) {
      return this._defaultValue;
    }
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

  @Generated("io.zrz.visitors")
  public abstract static class DefaultAbstractNoReturnVisitor implements NoReturnVisitor {
    protected abstract void visitDefault(Type value);

    @Override
    public void visitInputType(InputType value) {
      visitDefault(value);
    }

    @Override
    public void visitObjectType(ObjectType value) {
      visitDefault(value);
    }

    @Override
    public void visitEnumType(EnumType value) {
      visitDefault(value);
    }

    @Override
    public void visitInterfaceType(InterfaceType value) {
      visitDefault(value);
    }

    @Override
    public void visitScalarType(ScalarType value) {
      visitDefault(value);
    }

    @Override
    public void visitUnionType(UnionType value) {
      visitDefault(value);
    }
  }

  @Generated("io.zrz.visitors")
  public static class DefaultNoReturnVisitor extends DefaultAbstractNoReturnVisitor implements NoReturnVisitor {
    protected void visitDefault(Type value) {
    }
  }

  @Generated("io.zrz.visitors")
  public interface NoReturnGenericArgVisitor<R, V> {
    R visitInputType(InputType value, V param);

    R visitObjectType(ObjectType value, V param);

    R visitEnumType(EnumType value, V param);

    R visitInterfaceType(InterfaceType value, V param);

    R visitScalarType(ScalarType value, V param);

    R visitUnionType(UnionType value, V param);
  }

  @Generated("io.zrz.visitors")
  public abstract static class DefaultAbstractNoReturnGenericArgVisitor<R, V> implements NoReturnGenericArgVisitor<R, V> {
    protected abstract R visitDefault(Type value, V param);

    @Override
    public R visitInputType(InputType value, V param) {
      return visitDefault(value, param);
    }

    @Override
    public R visitObjectType(ObjectType value, V param) {
      return visitDefault(value, param);
    }

    @Override
    public R visitEnumType(EnumType value, V param) {
      return visitDefault(value, param);
    }

    @Override
    public R visitInterfaceType(InterfaceType value, V param) {
      return visitDefault(value, param);
    }

    @Override
    public R visitScalarType(ScalarType value, V param) {
      return visitDefault(value, param);
    }

    @Override
    public R visitUnionType(UnionType value, V param) {
      return visitDefault(value, param);
    }
  }

  @Generated("io.zrz.visitors")
  public static class DefaultNoReturnGenericArgVisitor<R, V> extends DefaultAbstractNoReturnGenericArgVisitor<R, V> implements NoReturnGenericArgVisitor<R, V> {
    protected final R _defaultValue;

    public DefaultNoReturnGenericArgVisitor(R defaultValue) {
      this._defaultValue = defaultValue;
    }

    public DefaultNoReturnGenericArgVisitor() {
      this._defaultValue = null;
    }

    protected R visitDefault(Type value, V param) {
      return this._defaultValue;
    }
  }
}
