package io.zrz.graphql.zulu.engine;

import java.util.List;

import zulu.runtime.subscriptions.ZuluResult;

public interface ZuluFullDataResult extends ZuluResult {

  List<ZuluWarning> accept(ZuluResultReceiver receiver);

}
