package io.zrz.graphql.zulu.engine;

import java.lang.reflect.Type;

import io.zrz.graphql.zulu.executable.ExecutableInput;

public interface ZuluRequestContext {

  Object parameter(String parameterName, ExecutableInput targetType);

  Object context(Type contextParameter);

}
