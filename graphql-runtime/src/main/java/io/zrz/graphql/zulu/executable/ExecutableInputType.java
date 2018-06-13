package io.zrz.graphql.zulu.executable;

import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import io.zrz.zulu.types.ZStructType;

public class ExecutableInputType implements ZStructType {

  private final ImmutableMap<String, ExecutableInputField> fields;

  public ExecutableInputType(ExecutableOutputField method, ImmutableList<ExecutableInputField> fields) {
    this.fields = fields.stream().collect(ImmutableMap.toImmutableMap(k -> k.fieldName(), k -> k));
  }

  @Override
  public Map<String, ExecutableInputField> fields() {
    return this.fields;
  }

}
