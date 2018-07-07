package io.zrz.graphql.zulu.engine;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

import com.google.common.base.Preconditions;

import io.zrz.graphql.zulu.doc.DefaultGQLPreparedOperation.OpInputField;
import io.zrz.graphql.zulu.executable.ExecutableInput;

class DefaultExecutionContext<RootT> implements ZuluContext {

  private final ZuluExecutable exec;
  private final RootT root;
  private final ZuluParameterReader defaultValues;
  private final ZuluExecutionScope scope;

  DefaultExecutionContext(final ZuluExecutable exec, final RootT root, final ZuluParameterReader defaultValues, final ZuluExecutionScope scope) {
    this.exec = exec;
    this.scope = scope;
    this.root = Objects.requireNonNull(root, "missing root instance");
    this.defaultValues = defaultValues;
  }

  @Override
  public ZuluExecutionResult execute(final ZuluRequest req, final ZuluResultReceiver receiver, final String fieldName) {
    final ZuluSelection sel = this.exec.selectionOrDefault(fieldName, null);
    Preconditions.checkArgument(sel != null, "operation does not define output field '%s'", fieldName);
    return this.execute(receiver, sel);
  }

  @Override
  public ZuluExecutionResult execute(final ZuluRequest req, final ZuluResultReceiver receiver) {

    final DefaultExecutionContext<RootT>.ExecutionState state = new ExecutionState(req, receiver);

    if (this.exec.inputType() != null) {

      Set<String> missing = null;

      for (final Entry<String, OpInputField> field : this.exec.inputType().fields().entrySet()) {

        if (!req.hasVariable(field.getKey())) {

          if (field.getValue().isOptional()) {
            continue;
          }

          if (missing == null) {
            missing = new HashSet<>();
          }

          missing.add(field.getKey());

          state.note(new ZuluWarning.MissingRequiredVariable(field.getValue(), this.exec), null);

        }

      }

      if (missing != null) {
        return state;
      }

    }

    receiver.push(this.exec, this.root);

    receiver.startStruct(this.exec, this.root);

    for (final ZuluSelection sel : this.exec.selections()) {
      sel.apply(state, this.root);
    }

    receiver.endStruct(this.exec, this.root);

    receiver.pop(this.exec, this.root);

    return state;

  }

  private ZuluExecutionResult execute(final ZuluResultReceiver receiver, final ZuluSelection selection) {
    final DefaultExecutionContext<RootT>.ExecutionState state = new ExecutionState(null, receiver);
    selection.apply(state, this.root);
    return state;
  }

  /**
   * internal per-execution state container which keeps track of the position of execution.
   */

  private class ExecutionState implements ZuluSelectionVisitor.ConsumerVisitor<Object>, ZuluExecutionResult, ZuluRequestContext {

    private final ZuluResultReceiver receiver;
    private ArrayList<ZuluWarning> notes = null;
    private final ZuluRequest req;

    public ExecutionState(final ZuluRequest req, final ZuluResultReceiver receiver) {
      this.receiver = receiver;
      this.req = req;
    }

    private void note(final ZuluWarning note, final ZuluSelection selection) {
      if (this.notes == null) {
        this.notes = new ArrayList<>();
      }
      this.notes.add(note);
    }

    @Override
    public void accept(final ZuluLeafSelection leaf, final Object value) {

      Object result;

      try {

        result = leaf.invoke(this, value);

        if (result == null && leaf.typeCritera().isPresent()) {
          // FIXME: this should actually check if the type matches or not.
          return;
        }

        if (leaf.isList()) {

          this.receiver.startList(leaf, this.receiver);

          try {
            if (result != null) {
              for (final Object element : (Object[]) result) {
                if (element == null)
                  this.receiver.write(leaf);
                else
                  this.receiver.write(leaf, element);
              }
            }
          }
          finally {

            this.receiver.endList(leaf, this.receiver);

          }

        }
        else {

          if (result == null) {
            // TODO: if the value is non-null, then propagate exception up.
            this.receiver.write(leaf);
          }
          else {
            this.receiver.write(leaf, result);
          }

        }

      }
      catch (final Exception ex) {
        this.note(new ZuluWarning.ExecutionError(leaf, ex, value), leaf);
        return;
      }
    }

    @Override
    public void accept(final ZuluContainerSelection container, final Object value) {
      try {

        Objects.requireNonNull(this.receiver);
        final Object context = container.invoke(this, value);

        if (context != null) {

          this.receiver.push(container, context);

          try {

            if (container.isList()) {

              this.receiver.startList(container, context);

              try {

                for (final Object element : (Object[]) context) {

                  this.receiver.next(element);

                  this.receiver.startStruct(container, null);
                  try {

                    container.selections().forEach(sub -> {

                      sub.apply(this, element);

                    });

                  }
                  finally {

                    this.receiver.endStruct(container, null);

                  }

                }

              }
              finally {
                this.receiver.endList(container, context);
              }

            }
            else {
              this.receiver.startStruct(container, null);
              try {
                container.selections().forEach(sub -> sub.apply(this, context));
              }
              finally {
                this.receiver.endStruct(container, null);
              }
            }

          }
          finally {
            this.receiver.pop(container, context);
          }

        }
        else {

          if (container.typeCritera().isPresent()) {
            // FIXME: this should actually check if the type matches or not.
            return;
          }

          // TODO: if it's a non-null return in the model, propogate up to parent.
          this.receiver.write(container);

        }
      }
      catch (final Exception ex) {
        this.note(new ZuluWarning.ExecutionError(container, ex, value), container);
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
    public Object parameter(final String parameterName, final ExecutableInput targetType) {
      final Object value = this.req.parameter(parameterName, targetType);
      return value;
    }

    /**
     * provide the context parameter.
     *
     * TODO: move to method handle model for early binding.
     *
     */

    @Override
    public Object context(final Type type) {

      return DefaultExecutionContext.this.scope.context(type);

    }

  }

}
