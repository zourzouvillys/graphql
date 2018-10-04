package io.zrz.graphql.zulu.engine;

import java.util.List;
import java.util.concurrent.Flow.Subscriber;

import hu.akarnokd.rxjava2.interop.FlowInterop;
import io.reactivex.Flowable;
import zulu.runtime.subscriptions.ZuluDataResult;
import zulu.runtime.subscriptions.ZuluResult;

public class ErrorPortal implements ZuluPortal, ZuluDataResult {

  private final ExecutionResult res;

  public ErrorPortal(final ExecutionResult res) {
    this.res = res;
  }

  @Override
  public void subscribe(final Subscriber<? super ZuluResult> subscriber) {

    Flowable.just(this)
        .to(FlowInterop.toFlow())
        .subscribe(subscriber);

  }

  @Override
  public List<ZuluWarning> notes() {
    return this.res.notes();
  }

  @Override
  public void cancel() {
  }

  @Override
  public void data(final ZuluResultReceiver receiver, final ZuluNotesReceiver notes) {
    notes.addAll(this.res.notes());
  }

  @Override
  public List<ZuluWarning> errors() {
    return this.res.notes();
  }

}
