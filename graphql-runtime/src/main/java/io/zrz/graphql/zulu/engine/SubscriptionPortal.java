package io.zrz.graphql.zulu.engine;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.Flow.Subscriber;

import com.google.common.collect.ImmutableList;

import hu.akarnokd.rxjava2.interop.FlowInterop;
import io.reactivex.Flowable;
import io.zrz.graphql.zulu.executable.ExecutableInput;
import zulu.runtime.subscriptions.ZuluDataResult;
import zulu.runtime.subscriptions.ZuluResult;

/**
 *
 * @author theo
 *
 */

// subscriptions are handled separately - we first use the instance to set up the subscription
// and then perform an execution on each value it returns. the main difference is the same field
// may be returned multiple times.

// flow control is important here - we may have a slow subscriber, and need to do some buffering and then
// abort the subscription with a buffer overflow error if they can't keep up (or switch to on-disk spooling,
// etc).

public class SubscriptionPortal implements ZuluPortal, ZuluRequestContext {

  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(SubscriptionPortal.class);
  private final ZuluExecutable executable;
  private final Object root;
  private final ZuluExecutionScope scope;
  private final ZuluRequest reqvars;
  private final ImmutableList<ZuluWarning> notes;

  public SubscriptionPortal(
      final ZuluExecutable executable,
      final Object root,
      final ZuluExecutionScope scope,
      final ZuluRequest reqvars,
      final ExecutionResult res) {

    this.executable = executable;
    this.root = root;
    this.scope = scope;
    this.reqvars = reqvars;
    this.notes = res.notes();

  }

  /**
   * A subscription portal will only start emiting events once there is a single subscriber, and continue until they
   * cancel, backpressure overrun occurs, or an internal error with the subscription.
   */

  @Override
  public void subscribe(final Subscriber<? super ZuluResult> subscriber) {

    //
    final Flowable<Flowable<? extends ZuluResult>> sources = Flowable
        .fromIterable(this.executable.selections())
        .doOnCancel(() -> log.debug("cancel"))
        .map(sel -> this.createHandle(sel));

    Flowable.mergeDelayError(sources)
        .doOnCancel(() -> log.debug("cancelled"))
        .to(FlowInterop.toFlow())
        .subscribe(subscriber);

  }

  @Override
  public void cancel() {
  }

  /**
   * fetch a subscription handle for this field.
   *
   * @param sel
   * @return
   */

  private Flowable<? extends ZuluResult> createHandle(final ZuluSelection sel) {

    // for each field on the root, we invoke it to get a subscription handle.
    final MethodHandle invoker = sel.invoker();

    try {

      final Flowable<? extends Object> handle = (Flowable<? extends Object>) invoker.invoke(this, this.root);

      return handle
          .filter(e -> e != null)
          .map(e -> this.mapResult(sel, e));

    }
    catch (final Throwable e) {
      return Flowable.error(e);
    }

  }

  private ZuluResult mapResult(final ZuluSelection sel, final Object value) {

    return new ZuluDataResult() {

      @Override
      public String toString() {
        return sel.fieldName() + ": " + value.toString() + " " + (sel.isLeaf() ? "LEAF" : "!LEAF");
      }

      @Override
      public void data(final ZuluResultReceiver receiver) {

        final ExecutionState<? extends Object> state = new ExecutionState<>(
            SubscriptionPortal.this.scope,
            SubscriptionPortal.this.reqvars,
            receiver);

        receiver.startStruct((ZuluSelectionContainer) sel, value);
        sel.apply(new Inner(state), value);
        receiver.endStruct((ZuluSelectionContainer) sel, value);

      }

    };
  }

  private class Inner implements ZuluSelectionVisitor.ConsumerVisitor<Object> {

    private final ExecutionState<? extends Object> state;

    public Inner(final ExecutionState<? extends Object> state) {
      this.state = state;
    }

    @Override
    public void accept(final ZuluLeafSelection leaf, final Object value) {
      // TODO Auto-generated method stub
      throw new UnsupportedOperationException("Unimplemented Method: ZuluSelectionVisitor.ConsumerVisitor<Object>.accept invoked.");
    }

    @Override
    public void accept(final ZuluContainerSelection container, final Object value) {
      container.selections().forEach(sel -> {

        sel.apply(this.state, value);

      });
    }

  }

  @Override
  public List<ZuluWarning> notes() {
    return this.notes;
  }

  @Override
  public Object parameter(final String parameterName, final ExecutableInput targetType) {
    return this.reqvars.parameter(parameterName, targetType);
  }

  @Override
  public Object context(final Type contextParameter) {
    return this.scope.context(contextParameter);
  }

}
