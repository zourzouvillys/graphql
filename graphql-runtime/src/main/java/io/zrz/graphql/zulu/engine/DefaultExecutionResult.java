package io.zrz.graphql.zulu.engine;

import org.immutables.value.Value;

@Value.Immutable
@Value.Style(deepImmutablesDetection = true, typeImmutable = "ExecutionResult", depluralize = true)
abstract class DefaultExecutionResult implements ZuluExecutionResult {

}
