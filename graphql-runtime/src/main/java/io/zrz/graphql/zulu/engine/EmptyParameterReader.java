package io.zrz.graphql.zulu.engine;

import io.zrz.graphql.zulu.executable.ExecutableInputField;

public enum EmptyParameterReader implements ZuluParameterReader {

  INSTANCE {

    @Override
    public Object get(final String parameterName, final ExecutableInputField targetType) {
      return null;
    }

    @Override
    public boolean has(final String parameterName) {
      return false;
    }

  }

}
