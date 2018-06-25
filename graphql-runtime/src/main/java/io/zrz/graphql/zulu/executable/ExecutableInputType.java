package io.zrz.graphql.zulu.executable;

import java.util.Map;
import java.util.Optional;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import io.zrz.graphql.zulu.LogicalTypeKind;
import io.zrz.graphql.zulu.executable.ExecutableSchemaBuilder.Symbol;
import io.zrz.zulu.types.ZStructType;

public class ExecutableInputType extends AbstractExecutableType implements ZStructType, ExecutableElement, ExecutableType {

  private final ImmutableMap<String, ExecutableInputField> fields;
  private String name;
  private ImmutableList<ExecutableInputField> list;

  /**
   * construct from a parameter list.
   *
   * @param method
   * @param fields
   */

  public ExecutableInputType(final ExecutableOutputField method, final ImmutableList<ExecutableInputField> fields) {
    this.fields = fields.stream().collect(ImmutableMap.toImmutableMap(k -> k.fieldName(), k -> k));
    this.list = fields;
  }

  /**
   * construct from a declared input type.
   *
   * @param schema
   * @param symbol
   * @param buildContext
   */

  public ExecutableInputType(final ExecutableSchema schema, final Symbol symbol, final BuildContext buildContext) {
    this.name = symbol.typeName;
    this.fields = ImmutableMap.of();
  }

  @Override
  public Optional<? extends ExecutableInputField> field(final String name) {
    return Optional.ofNullable(this.fields().get(name));
  }

  @Override
  public Map<String, ExecutableInputField> fields() {
    return this.fields;
  }

  public ImmutableList<ExecutableInputField> fieldValues() {
    return this.list;
  }

  @Override
  public LogicalTypeKind logicalKind() {
    return LogicalTypeKind.INPUT;
  }

  @Override
  public String typeName() {
    return this.name;
  }

  @Override
  public String documentation() {
    return null;
  }

}
