package io.zrz.graphql.zulu.engine;

import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

import com.google.common.base.Preconditions;

import io.zrz.graphql.zulu.doc.DefaultGQLPreparedOperation.OpInputField;

class DefaultExecutionContext<RootT> implements ZuluContext {

  private final ZuluExecutable exec;
  private final RootT root;
  private final ZuluParameterReader defaultValues;
  final ZuluExecutionScope scope;

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

    final ExecutionState<RootT> state = new ExecutionState<>(this.scope, req, receiver);

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

  // public static <RootT> ZuluExecutionResult apply(RootT root, final ZuluResultReceiver receiver, final ZuluSelection
  // selection) {
  // final DefaultExecutionContext<RootT>.ExecutionState state = new ExecutionState(null, receiver);
  // selection.apply(state, this.root);
  // return state;
  // }

  private ZuluExecutionResult execute(final ZuluResultReceiver receiver, final ZuluSelection selection) {
    final ExecutionState<RootT> state = new ExecutionState<>(this.scope, null, receiver);
    selection.apply(state, this.root);
    return state;
  }

}
