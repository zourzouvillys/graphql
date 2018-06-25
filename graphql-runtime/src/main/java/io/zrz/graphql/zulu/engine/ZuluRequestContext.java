package io.zrz.graphql.zulu.engine;

import io.zrz.graphql.zulu.executable.ExecutableInputField;

public interface ZuluRequestContext {

  Object parameter(String parameterName, ExecutableInputField targetType);

}
