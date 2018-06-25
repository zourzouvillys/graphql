package io.zrz.graphql.zulu.binding;

import java.lang.reflect.AnnotatedType;

import com.google.common.reflect.TypeToken;

import io.zrz.graphql.zulu.annotations.GQLMixin;

public class JavaBindingTypeUse {

  private final TypeToken<?> type;
  private final TypeToken<?> context;
  private final AnnotatedType annotatedType;

  public JavaBindingTypeUse(final TypeToken<?> context, final AnnotatedType type) {
    this.context = context;
    this.annotatedType = type;
    this.type = context.resolveType(type.getType());
  }

  public boolean isMixin() {
    return this.annotatedType.isAnnotationPresent(GQLMixin.class);
  }

  public Class<?> rawClass() {
    return this.type.getRawType();
  }

  public TypeToken<?> context() {
    return this.context;
  }

  public TypeToken<?> typeToken() {
    return this.type;
  }

  @Override
  public String toString() {
    return this.type.getType().toString() + (this.isMixin() ? " @GQLMixin" : "") + " on " + this.context;
  }

}
