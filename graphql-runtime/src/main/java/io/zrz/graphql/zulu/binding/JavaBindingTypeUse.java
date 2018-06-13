package io.zrz.graphql.zulu.binding;

import java.lang.reflect.AnnotatedType;

import com.google.common.reflect.TypeToken;

import io.zrz.graphql.zulu.annotations.GQLMixin;

public class JavaBindingTypeUse {

  private TypeToken<?> type;
  private TypeToken<?> context;
  private AnnotatedType annotatedType;

  public JavaBindingTypeUse(TypeToken<?> context, AnnotatedType type) {
    this.context = context;
    this.annotatedType = type;
    this.type = context.resolveType(type.getType());
  }

  public boolean isMixin() {
    return annotatedType.isAnnotationPresent(GQLMixin.class);
  }

  @Override
  public String toString() {
    return this.type.getType().toString() + (isMixin() ? " @Mixin" : "") + " on " + this.context;
  }

  public Class<?> rawClass() {
    return this.type.getRawType();
  }

  public TypeToken<?> typeToken() {
    return this.type;
  }

}
