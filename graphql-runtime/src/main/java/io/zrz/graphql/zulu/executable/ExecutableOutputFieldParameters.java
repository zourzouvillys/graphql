package io.zrz.graphql.zulu.executable;

import java.util.Map;
import java.util.Optional;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.TypeToken;

import io.zrz.graphql.zulu.LogicalTypeKind;
import io.zrz.zulu.types.ZStructType;

public class ExecutableOutputFieldParameters extends AbstractExecutableType implements ZStructType, ExecutableElement, ExecutableType {

  private final ImmutableMap<String, ExecutableOutputFieldParam> fields;
  private final ImmutableList<ExecutableOutputFieldParam> list;

  /**
   * construct from a parameter list.
   *
   * @param method
   * @param fields
   */

  public ExecutableOutputFieldParameters(final ExecutableOutputField method, final ImmutableList<ExecutableOutputFieldParam> fields) {
    this.fields = fields.stream().collect(ImmutableMap.toImmutableMap(k -> k.fieldName(), k -> k));
    this.list = fields;
  }

  @Override
  public Optional<? extends ExecutableOutputFieldParam> field(final String name) {
    return Optional.ofNullable(this.fields().get(name));
  }

  @Override
  public Map<String, ExecutableOutputFieldParam> fields() {
    return this.fields;
  }

  public ImmutableList<ExecutableOutputFieldParam> fieldValues() {
    return this.list;
  }

  @Override
  public LogicalTypeKind logicalKind() {
    return LogicalTypeKind.INPUT;
  }

  @Override
  public String typeName() {
    throw new IllegalArgumentException();
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
