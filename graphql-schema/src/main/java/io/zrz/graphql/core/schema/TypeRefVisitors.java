package io.zrz.graphql.core.schema;

import java.lang.Override;
import javax.annotation.Generated;

import io.zrz.graphql.core.schema.model.GenericTypeRef;
import io.zrz.graphql.core.schema.model.SimpleTypeRef;
import io.zrz.graphql.core.schema.model.Type;
import io.zrz.graphql.core.schema.model.TypeRef;

@Generated("io.zrz.visitors")
public final class TypeRefVisitors {
  @Generated("io.zrz.visitors")
  public interface GenericTypRefReturnVisitor<T extends Type, R> {
    R visitSimpleTypeRef(SimpleTypeRef<T> value);

    R visitGenericTypeRef(GenericTypeRef<T> value);
  }

  @Generated("io.zrz.visitors")
  public abstract static class DefaultAbstractGenericTypRefReturnVisitor<T extends Type, R> implements GenericTypRefReturnVisitor<T, R> {
    protected abstract R visitDefault(TypeRef<T> value);

    @Override
    public R visitSimpleTypeRef(SimpleTypeRef<T> value) {
      return visitDefault(value);
    }

    @Override
    public R visitGenericTypeRef(GenericTypeRef<T> value) {
      return visitDefault(value);
    }
  }

  @Generated("io.zrz.visitors")
  public static class DefaultGenericTypRefReturnVisitor<T extends Type, R> extends DefaultAbstractGenericTypRefReturnVisitor<T, R> implements GenericTypRefReturnVisitor<T, R> {
    protected final R _defaultValue;

    public DefaultGenericTypRefReturnVisitor(R defaultValue) {
      this._defaultValue = defaultValue;
    }

    public DefaultGenericTypRefReturnVisitor() {
      this._defaultValue = null;
    }

    protected R visitDefault(TypeRef<T> value) {
      return this._defaultValue;
    }
  }

  @Generated("io.zrz.visitors")
  public interface NoReturnVisitor<T extends Type> {
    void visitSimpleTypeRef(SimpleTypeRef<T> value);

    void visitGenericTypeRef(GenericTypeRef<T> value);
  }

  @Generated("io.zrz.visitors")
  public abstract static class DefaultAbstractNoReturnVisitor<T extends Type> implements NoReturnVisitor<T> {
    protected abstract void visitDefault(TypeRef<T> value);

    @Override
    public void visitSimpleTypeRef(SimpleTypeRef<T> value) {
      visitDefault(value);
    }

    @Override
    public void visitGenericTypeRef(GenericTypeRef<T> value) {
      visitDefault(value);
    }
  }

  @Generated("io.zrz.visitors")
  public static class DefaultNoReturnVisitor<T extends Type> extends DefaultAbstractNoReturnVisitor<T> implements NoReturnVisitor<T> {
    protected void visitDefault(TypeRef<T> value) {
    }
  }

  @Generated("io.zrz.visitors")
  public interface NoReturnGenericArgVisitor<T extends Type, R, V> {
    R visitSimpleTypeRef(SimpleTypeRef<T> value, V param);

    R visitGenericTypeRef(GenericTypeRef<T> value, V param);
  }

  @Generated("io.zrz.visitors")
  public abstract static class DefaultAbstractNoReturnGenericArgVisitor<T extends Type, R, V> implements NoReturnGenericArgVisitor<T, R, V> {
    protected abstract R visitDefault(TypeRef<T> value, V param);

    @Override
    public R visitSimpleTypeRef(SimpleTypeRef<T> value, V param) {
      return visitDefault(value, param);
    }

    @Override
    public R visitGenericTypeRef(GenericTypeRef<T> value, V param) {
      return visitDefault(value, param);
    }
  }

  @Generated("io.zrz.visitors")
  public static class DefaultNoReturnGenericArgVisitor<T extends Type, R, V> extends DefaultAbstractNoReturnGenericArgVisitor<T, R, V> implements NoReturnGenericArgVisitor<T, R, V> {
    protected final R _defaultValue;

    public DefaultNoReturnGenericArgVisitor(R defaultValue) {
      this._defaultValue = defaultValue;
    }

    public DefaultNoReturnGenericArgVisitor() {
      this._defaultValue = null;
    }

    protected R visitDefault(TypeRef<T> value, V param) {
      return this._defaultValue;
    }
  }
}
