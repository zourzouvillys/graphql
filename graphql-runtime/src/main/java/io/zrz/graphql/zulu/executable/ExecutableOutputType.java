package io.zrz.graphql.zulu.executable;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.common.collect.ImmutableMap;

import io.zrz.graphql.zulu.LogicalTypeKind;
import io.zrz.graphql.zulu.ZOutputType;
import io.zrz.graphql.zulu.executable.ExecutableSchemaBuilder.Symbol;
import io.zrz.zulu.types.ZTypeKind;

public final class ExecutableOutputType implements ExecutableType, ZOutputType {

  private ExecutableSchema schema;
  private String typeName;
  private ImmutableMap<String, ExecutableOutputField> fields;

  ExecutableOutputType(ExecutableSchema schema, Symbol symbol, BuildContext types) {
    types.add(symbol, this);
    this.schema = schema;
    this.typeName = symbol.typeName;
    this.fields = symbol.handle.outputFields()
        .map(field -> new ExecutableOutputField(this, symbol, field, types))
        .collect(ImmutableMap.toImmutableMap(k -> k.fieldName(), k -> k));
  }

  public ExecutableSchema schema() {
    return this.schema;
  }

  /**
   * each executable has an app specific context value which represents the specific instance of the type being operated
   * on.
   * 
   * for the query root type, this is normally a "viewer" type, which represents the entry point into the model. for
   * type nodes it would normally be something representing that type, e.g an instance of a java type representing it or
   * a database identifier.
   * 
   * this value is normally only relevant to the caller for the root types (and is normally the root type itself), as it
   * must pass it in to execute anything.
   * 
   * this will always return a logical type of kind {@link io.zrz.graphql.zulu.LogicalTypeKind.LogicalTypeKind#OUTPUT}.
   * 
   */

  public ExecutableTypeUse contextType() {
    return null;
  }

  /**
   * the fields in this output type.
   */

  @Override
  public Map<String, ExecutableOutputField> fields() {
    return this.fields;
  }

  @Override
  public Optional<ExecutableOutputField> field(String name) {
    return Optional.ofNullable(this.fields().get(name));
  }

  @Override
  public ZTypeKind typeKind() {
    return ZTypeKind.STRUCT;
  }

  @Override
  public String typeName() {
    return this.typeName;
  }

  @Override
  public LogicalTypeKind logicalKind() {
    return LogicalTypeKind.OUTPUT;
  }

  public List<String> documentation() {
    return Collections.emptyList();
  }

}
