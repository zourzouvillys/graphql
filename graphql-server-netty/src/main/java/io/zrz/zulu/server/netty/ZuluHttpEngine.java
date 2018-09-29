package io.zrz.zulu.server.netty;

import io.reactivex.Flowable;

public interface ZuluHttpEngine {

  Flowable<ZuluJacksonResult> execute(HttpOperationRequest req);

}
