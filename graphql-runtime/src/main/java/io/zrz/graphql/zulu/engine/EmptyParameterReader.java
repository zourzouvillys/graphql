package io.zrz.graphql.zulu.engine;

import io.zrz.graphql.zulu.executable.ExecutableTypeUse;

public enum EmptyParameterReader implements ZuluParameterReader {

  INSTANCE {

    @Override
    public Object get(String parameterName, ExecutableTypeUse targetType) {
      return null;
    }

  }

}
