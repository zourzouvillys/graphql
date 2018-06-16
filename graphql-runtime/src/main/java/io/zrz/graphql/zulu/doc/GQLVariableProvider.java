package io.zrz.graphql.zulu.doc;

import java.util.Optional;

import io.zrz.graphql.core.value.GQLVariableRef;
import io.zrz.zulu.values.ZValue;

public interface GQLVariableProvider {

  Optional<ZValue> resolve(GQLVariableRef value);

}
