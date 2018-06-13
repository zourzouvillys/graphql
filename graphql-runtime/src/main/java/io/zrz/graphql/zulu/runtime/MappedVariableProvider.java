package io.zrz.graphql.zulu.runtime;

import java.util.Optional;

import io.zrz.graphql.core.value.GQLVariableRef;
import io.zrz.zulu.values.ZStructValue;
import io.zrz.zulu.values.ZValue;

public class MappedVariableProvider implements GQLVariableProvider {

  private ZStructValue inputArgs;

  public MappedVariableProvider(ZStructValue inputArgs) {
    this.inputArgs = inputArgs;
  }

  @Override
  public Optional<ZValue> resolve(GQLVariableRef value) {
    return inputArgs.fieldValue(value.name());
  }

}
