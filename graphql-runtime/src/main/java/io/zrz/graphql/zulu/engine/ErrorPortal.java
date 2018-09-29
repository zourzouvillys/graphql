package io.zrz.graphql.zulu.engine;

import java.util.List;
import java.util.concurrent.Flow.Subscriber;

import zulu.runtime.subscriptions.ZuluResult;

public class ErrorPortal implements ZuluPortal {

  private final ExecutionResult res;

  public ErrorPortal(final ExecutionResult res) {
    this.res = res;
  }

  @Override
  public void subscribe(final Subscriber<? super ZuluResult> subscriber) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented Method: ErrorPortal.subscribe invoked.");
  }

  @Override
  public List<ZuluWarning> notes() {
    return this.res.notes();
  }

  @Override
  public void cancel() {
  }

}
