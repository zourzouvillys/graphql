package io.zrz.zulu.schema;

import java.util.Map;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import io.zrz.graphql.core.runtime.GQLOperationType;

public class ResolvedSchema {

  private ImmutableMap<String, ResolvedType> types;
  private ImmutableMap<GQLOperationType, ResolvedType> operationRoots;

  public ResolvedSchema(SchemaCompiler compiler, Map<GQLOperationType, String> ops) {

    this.operationRoots = ops.entrySet()
        .stream()
        .collect(ImmutableMap.toImmutableMap(e -> e.getKey(), e -> compiler.build(this, e.getValue())));

    this.types = ImmutableMap.copyOf(compiler.registered);

  }

  public ResolvedType operationType(GQLOperationType op) {
    return this.operationRoots.get(op);
  }

  public ResolvedType type(String typeName) {
    return this.types.get(typeName);
  }

  public ImmutableSet<String> typeNames() {
    return this.types.keySet();
  }

  public ImmutableCollection<ResolvedType> types() {
    return this.types.values();
  }

}
