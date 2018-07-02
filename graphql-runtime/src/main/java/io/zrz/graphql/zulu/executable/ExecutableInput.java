package io.zrz.graphql.zulu.executable;

import java.util.Optional;

import org.eclipse.jdt.annotation.NonNull;

import com.google.common.reflect.TypeToken;

import io.zrz.zulu.values.ZValue;

public interface ExecutableInput {

  ExecutableTypeUse fieldType();

  @NonNull
  String fieldName();

  boolean isNullable();

  Optional<ZValue> defaultValue();

  TypeToken<?> javaType();

}
