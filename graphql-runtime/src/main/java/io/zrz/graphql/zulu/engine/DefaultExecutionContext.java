package io.zrz.graphql.zulu.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.google.common.base.Preconditions;

import io.zrz.graphql.zulu.executable.ExecutableTypeUse;

class DefaultExecutionContext<RootT> implements ZuluContext {

  private ZuluExecutable exec;
  private RootT root;
  private ZuluParameterReader defaultValues;

  DefaultExecutionContext(ZuluExecutable exec, RootT root, ZuluParameterReader defaultValues) {
    this.exec = exec;
    this.root = Objects.requireNonNull(root, "missing root instance");
    this.defaultValues = defaultValues;
  }

  @Override
  public ZuluExecutionResult execute(ZuluRequest req, ZuluResultReceiver receiver, String fieldName) {
    ZuluSelection sel = exec.selectionOrDefault(fieldName, null);
    Preconditions.checkArgument(sel != null, "operation does not define output field '%s'", fieldName);
    return execute(receiver, sel);
  }

  @Override
  public ZuluExecutionResult execute(ZuluRequest req, ZuluResultReceiver receiver) {

    DefaultExecutionContext<RootT>.ExecutionState state = new ExecutionState(req, receiver);

    receiver.push(this.exec, root);

    receiver.startStruct(this.exec, root);

    for (ZuluSelection sel : exec.selections()) {
      sel.apply(state, root);
    }

    receiver.endStruct(this.exec, root);

    receiver.pop(this.exec, root);

    return state;

  }

  private ZuluExecutionResult execute(ZuluResultReceiver receiver, ZuluSelection selection) {
    DefaultExecutionContext<RootT>.ExecutionState state = new ExecutionState(null, receiver);
    selection.apply(state, root);
    return state;
  }

  /**
   * internal per-execution state container which keeps track of the position of execution.
   */

  private class ExecutionState implements ZuluSelectionVisitor.ConsumerVisitor<Object>, ZuluExecutionResult, ZuluRequestContext {

    private ZuluResultReceiver receiver;
    private ArrayList<ZuluWarning> notes = null;
    private ZuluRequest req;

    public ExecutionState(ZuluRequest req, ZuluResultReceiver receiver) {
      this.receiver = receiver;
      this.req = req;
    }

    private void note(ZuluWarning note, ZuluSelection selection) {
      if (this.notes == null) {
        this.notes = new ArrayList<>();
      }
      this.notes.add(note);
    }

    @Override
    public void accept(ZuluLeafSelection leaf, Object value) {
      Object result;
      try {
        result = leaf.invoke(this, value);

        if (leaf.isList()) {
          receiver.startList(leaf, receiver);
          if (result == null) {
          }
          else {
            for (Object element : (Object[]) result) {
              receiver.write(leaf, element);
            }
          }
          receiver.endList(leaf, receiver);
        }
        else {

          if (result == null) {
            // TODO: if the value is non-null, then propagate exception up.
            receiver.write(leaf);
          }
          else {
            receiver.write(leaf, result);
          }

        }

      }
      catch (Exception ex) {
        note(new ZuluWarning.ExecutionError(leaf, ex, value), leaf);
        return;
      }
    }

    @Override
    public void accept(ZuluContainerSelection container, Object value) {
      try {

        Objects.requireNonNull(receiver);
        Object context = container.invoke(this, value);

        if (context != null) {

          receiver.push(container, context);
          try {

            if (container.isList()) {

              receiver.startList(container, context);

              for (Object element : (Object[]) context) {
                receiver.next(element);
                container.selections().forEach(sub -> {

                  receiver.startStruct(container, null);
                  sub.apply(this, element);
                  receiver.endStruct(container, null);

                });
              }

              receiver.endList(container, context);

            }
            else {
              receiver.startStruct(container, null);
              container.selections().forEach(sub -> sub.apply(this, context));
              receiver.endStruct(container, null);
            }

          }
          finally {
            receiver.pop(container, context);
          }
        }
        else {
          // TODO: if it's a non-null return in the model, propogate up to parent.
        }
      }
      catch (Exception ex) {
        note(new ZuluWarning.ExecutionError(container, ex, value), container);
        return;
      }
    }

    @Override
    public List<ZuluWarning> notes() {
      if (this.notes == null) {
        return Collections.emptyList();
      }
      return this.notes;
    }

    @Override
    public Object parameter(String parameterName, ExecutableTypeUse targetType) {
      return req.parameter(parameterName, targetType);
    }

  }

}
