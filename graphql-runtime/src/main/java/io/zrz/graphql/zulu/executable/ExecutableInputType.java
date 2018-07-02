package io.zrz.graphql.zulu.executable;

import java.util.Map;
import java.util.Optional;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.TypeToken;

import io.zrz.graphql.zulu.LogicalTypeKind;
import io.zrz.graphql.zulu.executable.ExecutableSchemaBuilder.Symbol;
import io.zrz.zulu.types.ZStructType;

public class ExecutableInputType extends AbstractExecutableType implements ZStructType, ExecutableElement, ExecutableType {

  private final ImmutableMap<String, ExecutableInputField> fields;
  private final String name;
  private final ImmutableList<ExecutableInputField> list;

  /**
   * construct from a declared input type.
   *
   * @param schema
   * @param symbol
   * @param buildContext
   */

  public ExecutableInputType(final ExecutableSchema schema, final Symbol symbol, final BuildContext buildContext) {

    buildContext.add(symbol, this);

    this.name = symbol.typeName;

    final ZStructType bean = symbol.ztype != null
        ? (ZStructType) symbol.ztype
        : (ZStructType) buildContext.builder().typeBinder().scan(symbol.typeToken.getType());

    this.list = bean
        .fields()
        .values()
        .stream()
        .map(p -> new ExecutableInputField(this, p, buildContext))
        .collect(ImmutableList.toImmutableList());

    this.fields = this.list.stream()
        .collect(ImmutableMap.toImmutableMap(e -> e.fieldName(), e -> e));

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

  @Override
  public TypeToken<?> javaType() {
    throw new IllegalStateException("no java type for input type");
  }

}
