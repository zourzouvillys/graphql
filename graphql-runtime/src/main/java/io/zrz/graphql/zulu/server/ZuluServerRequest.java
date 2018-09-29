package io.zrz.graphql.zulu.server;

import java.util.List;

import org.eclipse.jdt.annotation.Nullable;
import org.immutables.value.Value;

import io.zrz.graphql.zulu.engine.ZuluParameterReader;
import io.zrz.graphql.zulu.engine.ZuluResultReceiver;

@Value.Immutable
@Value.Style(deepImmutablesDetection = true)
public interface ZuluServerRequest {

  List<ImmutableQuery> queries();

  ZuluInjector injector();

  @Value.Immutable
  public interface Query {

    @Nullable
    String query();

    @Nullable
    String operationName();

    ZuluParameterReader variables();

    ZuluResultReceiver resultReceiver();

    @Nullable
    String persistedQuery();

  }

  @Value.Immutable
  public interface BindParams {

    @Nullable
    String query();

    @Nullable
    String operationName();

    @Nullable
    ZuluParameterReader variables();

    @Nullable
    String persistedQuery();

  }

}
