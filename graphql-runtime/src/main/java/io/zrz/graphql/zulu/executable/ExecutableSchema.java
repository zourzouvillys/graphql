package io.zrz.graphql.zulu.executable;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

import io.zrz.graphql.core.doc.GQLOpType;
import io.zrz.graphql.core.runtime.GQLOperationType;
import io.zrz.graphql.zulu.LogicalTypeKind;
import io.zrz.zulu.types.ZType;

/**
 * a resolved and ready-to-execute schema bound to the java and graphql domain.
 * 
 * @author theo
 *
 */

public class ExecutableSchema implements ExecutableElement {

  private final ImmutableMap<GQLOperationType, ExecutableOutputType> operationRoots;
  private final ImmutableMap<String, ExecutableType> types;

  ExecutableSchema(ExecutableSchemaBuilder b) {

    BuildContext ctx = new BuildContext(b, this);

    Builder<GQLOperationType, ExecutableOutputType> roots = ImmutableMap.<GQLOperationType, ExecutableOutputType>builder();

    b.operationRoots()
        .forEach((type, symbol) -> roots.put(type, (ExecutableOutputType) ctx.compile(symbol)));

    this.operationRoots = roots.build();

    this.types = ctx.types
        .entrySet()
        .stream()
        .collect(ImmutableMap.toImmutableMap(k -> k.getKey().typeName, k -> k.getValue(), (t1, t2) -> {

          return t1;

        }));

  }

  /**
   * fetches the root type for a specific operation on this schema.
   * 
   * this would normally be {@link GQLOpType#Query}, {@link GQLOpType#Mutation}, or {@link GQLOpType#Subscription}.
   * 
   */

  public Optional<ExecutableOutputType> rootType(GQLOperationType type) {
    return Optional.ofNullable(this.operationRoots.get(type));
  }

  /**
   * the name of each GraphQL type in this schema.
   */

  public Stream<String> typeNames() {
    return types.keySet().stream();
  }

  /**
   * returns the ZType that represents the given GraphQL type name.
   */

  public ZType type(String typeName) {
    return null;
  }

  public Stream<ExecutableType> types() {
    return this.types.values().stream();
  }

  public Map<GQLOperationType, ExecutableOutputType> operationTypes() {
    return this.operationRoots;

  }

  public ExecutableOutputField resolve(String typeName, String fieldName) {

    ExecutableType type = this.types.get(typeName);

    if (type.logicalKind() != LogicalTypeKind.OUTPUT) {
      throw new IllegalArgumentException("type '" + typeName + "' is not an output type");
    }

    return resolve((ExecutableOutputType) type, fieldName);

  }

  private ExecutableOutputField resolve(ExecutableOutputType type, String fieldName) {
    return type.field(fieldName)
        .orElseThrow(() -> type.missingFieldException(fieldName));
  }

  public static ExecutableSchemaBuilder builder() {
    return new ExecutableSchemaBuilder();
  }

  public static ExecutableSchema withRoot(Type queryRoot) {
    return builder()
        .setRootType(GQLOpType.Query, queryRoot)
        .build();
  }

  public static ExecutableSchema withRoot(Type queryRoot, Type mutationRoot) {
    return builder()
        .setRootType(GQLOpType.Query, queryRoot)
        .setRootType(GQLOpType.Mutation, mutationRoot)
        .build();
  }

  /**
   * finds the type with the specified name.
   * 
   * @param typeName
   * @return
   */

  public ExecutableType resolveType(String typeName) {
    return this.types.get(typeName);
  }

}
