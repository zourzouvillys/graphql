package io.zrz.graphql.zulu.engine;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import io.zrz.graphql.zulu.executable.ExecutableInput;

/**
 * internal per-execution state container which keeps track of the position of execution.
 */

class ExecutionState<RootT> implements ZuluSelectionVisitor.ConsumerVisitor<Object>, ZuluExecutionResult, ZuluRequestContext {

  /**
   *
   */
  private final ZuluExecutionScope scope;
  private final ZuluResultReceiver receiver;
  private ArrayList<ZuluWarning> notes = null;
  private final ZuluRequest req;

  public ExecutionState(final ZuluExecutionScope scope, final ZuluRequest req, final ZuluResultReceiver receiver) {
    this.scope = scope;
    this.receiver = receiver;
    this.req = req;
  }

  void note(final ZuluWarning note, final ZuluSelection selection) {
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

      if ((result == null) && leaf.typeCritera().isPresent()) {
        // FIXME: this should actually check if the type matches or not.
        return;
      }

      if (leaf.isList()) {

        this.receiver.startList(leaf, this.receiver);

        try {
          if (result != null) {
            for (final Object element : (Object[]) result) {
              if (element == null) {
                this.receiver.write(leaf);
              }
              else {
                this.receiver.write(leaf, element);
              }
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
    return this.scope.context(type);
  }

}
