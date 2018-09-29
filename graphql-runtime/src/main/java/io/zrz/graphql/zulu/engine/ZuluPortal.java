package io.zrz.graphql.zulu.engine;

import java.util.List;
import java.util.concurrent.Flow;

import zulu.runtime.subscriptions.ZuluResult;

public interface ZuluPortal extends Flow.Publisher<ZuluResult> {

  List<ZuluWarning> notes();

  void cancel();

}
