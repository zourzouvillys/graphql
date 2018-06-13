package io.zrz.graphql.zulu.executable;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

import io.zrz.graphql.core.doc.GQLOpType;
import io.zrz.graphql.core.runtime.GQLOperationType;
import io.zrz.graphql.zulu.executable.ExecutableSchemaBuilder.Symbol;
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

    Map<Symbol, ExecutableType> types = ctx.types;

    Builder<GQLOperationType, ExecutableOutputType> roots = ImmutableMap.<GQLOperationType, ExecutableOutputType>builder();

    b.operationRoots().forEach((type, symbol) -> roots.put(type, (ExecutableOutputType) ctx.compile(symbol)));

    // while (!ctx.pending().isEmpty()) {
    // Symbol symbol = ctx.pending().iterator().next();
    // ctx.types.put(symbol, new ExecutableOutputType(this, symbol, ctx));
    // Preconditions.checkState(!ctx.pending().contains(symbol));
    // }

    this.operationRoots = roots.build();

    this.types = types
        .entrySet()
        .stream()
        .collect(ImmutableMap.toImmutableMap(k -> k.getKey().typeName, k -> k.getValue()));

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

}
