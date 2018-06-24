package io.zrz.graphql.zulu.engine;

import io.zrz.graphql.zulu.executable.ExecutableTypeUse;

public interface ZuluRequestContext {

  Object parameter(String parameterName, ExecutableTypeUse targetType);

}
