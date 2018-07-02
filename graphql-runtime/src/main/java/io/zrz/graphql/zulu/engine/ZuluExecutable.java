package io.zrz.graphql.zulu.engine;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.TypeToken;

import io.zrz.graphql.core.runtime.GQLOperationType;
import io.zrz.graphql.zulu.doc.DefaultGQLPreparedOperation.OpInputType;
import io.zrz.graphql.zulu.executable.ExecutableElement;
import io.zrz.graphql.zulu.executable.ExecutableOutputType;

public class ZuluExecutable implements ZuluSelectionContainer, ExecutableElement {

  private final ImmutableList<ZuluSelection> selections;
  private final ExecutableOutputType outputType;
  private final ImmutableMap<String, ZuluSelection> fields;
  private final ImmutableList<String> outputNames;
  private final Optional<String> operationName;
  private final GQLOperationType operationType;
  private final OpInputType inputType;

  /**
   * builds an immutable version in a single shot. delegates to the builder.
   *
   * @param type
   */

  ZuluExecutable(final ExecutableBuilder builder, final ExecutableOutputType type) {
    this.outputType = type;
    this.selections = builder.build(this);
    this.operationName = builder.operationName();
    this.operationType = builder.operationType();
    this.outputNames = this.selections.stream().sequential().map(s -> s.outputName()).collect(ImmutableList.toImmutableList());
    this.fields = this.selections.stream().sequential().collect(ImmutableBiMap.toImmutableBiMap(s -> s.outputName(), s -> s));
    this.inputType = builder.inputType();
  }

  public ImmutableList<ZuluSelection> selections() {
    return this.selections;
  }

  public List<String> fields() {
    return this.outputNames;
  }

  public <RootT> ZuluContext bind(final RootT root, final ZuluParameterReader reader, final ZuluExecutionScope scope) {
    return new DefaultExecutionContext<>(this, Objects.requireNonNull(root), reader, scope);
  }

  public <RootT> ZuluContext bind(final RootT root, final ZuluExecutionScope scope) {
    return this.bind(root, EmptyParameterReader.INSTANCE, scope);
  }

  @Override
  public ZuluExecutable executable() {
    return this;
  }

  /**
   * the underlying java type token for the root type of this executable.
   */

  public TypeToken<?> javaType() {
    return this.outputType.javaType();
  }

  @Override
  public ExecutableOutputType outputType() {
    return this.outputType;
  }

  public ZuluSelection selectionOrDefault(final String fieldName, final ZuluSelection defaultValue) {
    final ZuluSelection field = this.fields.get(fieldName);
    if (field == null)
      return defaultValue;
    return field;
  }

  @Override
  public String outputName() {
    return null;
  }

  @Override
  public boolean isList() {
    return false;
  }

  public Optional<String> operationName() {
    return this.operationName;
  }

  public GQLOperationType operationType() {
    return this.operationType;
  }

  public OpInputType inputType() {
    return this.inputType;
  }

  @Deprecated
  public ZuluExecutionResult execute(final ZuluRequest req, final ZuluResultReceiver receiver) {

    final ZuluExecutionScope scope = new ZuluExecutionScope();

    try {

      Object instance;
      try {
        instance = this.javaType()
            .getRawType()
            .getDeclaredConstructor()
            .newInstance();
      }
      catch (final RuntimeException e) {
        throw e;
      }
      catch (final Throwable e) {
        throw new RuntimeException(e);
      }

      // bind to the context for this caller.
      final ZuluContext ctx = this.bind(instance, scope);

      final ZuluExecutionResult execres = ctx.execute(req, receiver);

      final ExecutionResult.Builder res = ExecutionResult.builder();

      res.addAllNotes(execres.notes());

      // and execute it to get result.
      final ExecutionResult eval = res.build();

      scope.complete(new ZuluExecutionResult[] { eval });

      return eval;

    }
    catch (final Throwable t) {

      scope.error(t);
      throw t;

    }

  }

}
