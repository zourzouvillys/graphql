package io.zrz.graphql.zulu.engine;

import java.util.List;
import java.util.concurrent.Flow.Subscriber;

import com.google.common.collect.ImmutableList;

import hu.akarnokd.rxjava2.interop.FlowInterop;
import io.reactivex.Flowable;
import zulu.runtime.subscriptions.ZuluResult;

public class ImmediatePortal implements ZuluPortal, ZuluFullDataResult {

  private final ZuluExecutable executable;
  private final Object instance;
  private final ZuluExecutionScope scope;
  private final ZuluRequest reqvars;
  private final ImmutableList<ZuluWarning> notes;

  public ImmediatePortal(final ZuluExecutable executable, final Object instance, final ZuluExecutionScope scope, final ZuluRequest reqvars,
      final ExecutionResult res) {
    this.executable = executable;
    this.instance = instance;
    this.scope = scope;
    this.reqvars = reqvars;
    this.notes = res.notes();
  }

  @Override
  public List<ZuluWarning> notes() {
    return this.notes;
  }

  @Override
  public void subscribe(final Subscriber<? super ZuluResult> subscriber) {
    Flowable.just(this).to(FlowInterop.toFlow()).subscribe(subscriber);
  }

  @Override
  public List<ZuluWarning> accept(final ZuluResultReceiver receiver) {

    final ZuluContext ctx = this.executable.bind(this.instance, this.scope);

    // execute
    final ZuluExecutionResult execres = ctx.execute(this.reqvars, null);

    // additional runtime notes.
    return execres.notes();

    // add the notes from execution to the response.
    // res.addAllNotes(execres.notes());

    // and execute it.
    // return res.build();

  }

  @Override
  public void cancel() {
  }

}
