package io.zrz.graphql.zulu.runtime;

import java.util.Optional;

import io.zrz.graphql.core.value.GQLVariableRef;
import io.zrz.zulu.values.ZValue;

public class EmptyGQLVariableProvider implements GQLVariableProvider {

  public EmptyGQLVariableProvider() {
  }

  @Override
  public Optional<ZValue> resolve(GQLVariableRef value) {
    return Optional.empty();
  }

}
