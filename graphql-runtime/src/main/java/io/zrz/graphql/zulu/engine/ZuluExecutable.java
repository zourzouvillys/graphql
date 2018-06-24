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
import io.zrz.graphql.zulu.executable.ExecutableOutputType;
import io.zrz.zulu.types.ZStructType;

public class ZuluExecutable implements ZuluSelectionContainer {

  private ImmutableList<ZuluSelection> selections;
  private ExecutableOutputType outputType;
  private ImmutableMap<String, ZuluSelection> fields;
  private ImmutableList<String> outputNames;
  private Optional<String> operationName;
  private GQLOperationType operationType;
  private OpInputType inputType;

  /**
   * builds an immutable version in a single shot. delegates to the builder.
   * 
   * @param type
   */

  ZuluExecutable(ExecutableBuilder builder, ExecutableOutputType type) {
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

  public <RootT> ZuluContext bind(RootT root, ZuluParameterReader reader) {
    return new DefaultExecutionContext<>(this, Objects.requireNonNull(root), reader);
  }

  public <RootT> ZuluContext bind(RootT root) {
    return bind(root, EmptyParameterReader.INSTANCE);
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
    return outputType;
  }

  public ZuluSelection selectionOrDefault(String fieldName, ZuluSelection defaultValue) {
    ZuluSelection field = this.fields.get(fieldName);
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

}
