package io.zrz.graphql.zulu.executable;

import java.lang.reflect.Type;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.reflect.TypeToken;

import io.zrz.graphql.core.doc.GQLOpType;
import io.zrz.graphql.core.runtime.GQLOperationType;
import io.zrz.graphql.zulu.LogicalTypeKind;
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
  private ImmutableMap<TypeToken<?>, ExecutableType> tokens;

  ExecutableSchema(final ExecutableSchemaBuilder b) {

    final BuildContext ctx = new BuildContext(b, this);

    final Builder<GQLOperationType, ExecutableOutputType> roots = ImmutableMap.<GQLOperationType, ExecutableOutputType>builder();

    b.operationRoots()
        .entrySet()
        .stream()
        .sequential()
        .sorted(Comparator.comparing(Entry::getValue, Comparator.comparing(Symbol::typeName)))
        .forEach(e -> roots.put(e.getKey(), (ExecutableOutputType) ctx.compile(e.getValue())));

    this.operationRoots = roots.build();

    // we also need to force-add any types which are not exposed directly through params/return types but implement
    // interfaces which are.

    b.additionalTypes(ctx.types.keySet())
        .sorted(Comparator.comparing(Symbol::typeName))
        .forEach(sym -> ctx.use(this, sym.typeToken));

    this.types = ctx.types
        .entrySet()
        .stream()
        .sorted(Comparator.comparing(Entry::getKey, Comparator.comparing(Symbol::typeName)))
        .collect(ImmutableMap.toImmutableMap(k -> k.getKey().typeName, k -> k.getValue(), (t1, t2) -> {

          return t1;

        }));

    this.tokens = ctx.types
        .entrySet()
        .stream()
        .sorted(Comparator.comparing(Entry::getKey, Comparator.comparing(Symbol::typeName)))
        .collect(ImmutableMap.toImmutableMap(k -> k.getKey().typeToken, k -> k.getValue(), (t1, t2) -> {

          throw new IllegalArgumentException(
              "type registered multiple times:\n - " + t1 + " (" + t1.typeName() + ")" + "\n - " + t2 + " (" + t2.typeName() + ")");

        }));

  }

  /**
   * fetches the root type for a specific operation on this schema.
   *
   * this would normally be {@link GQLOpType#Query}, {@link GQLOpType#Mutation}, or {@link GQLOpType#Subscription}.
   *
   */

  public Optional<ExecutableOutputType> rootType(final GQLOperationType type) {
    return Optional.ofNullable(this.operationRoots.get(type));
  }

  /**
   * the name of each GraphQL type in this schema.
   */

  public Stream<String> typeNames() {
    return this.types.keySet().stream();
  }

  /**
   * returns the ZType that represents the given GraphQL type name.
   */

  public ZType type(final String typeName) {
    return null;
  }

  public Stream<ExecutableType> types() {
    return this.types.values().stream();
  }

  public Map<GQLOperationType, ExecutableOutputType> operationTypes() {
    return this.operationRoots;

  }

  public ExecutableOutputField resolve(final String typeName, final String fieldName) {

    final ExecutableType type = this.types.get(typeName);

    if (type.logicalKind() != LogicalTypeKind.OUTPUT) {
      throw new IllegalArgumentException("type '" + typeName + "' is not an output type");
    }

    return this.resolve((ExecutableOutputType) type, fieldName);

  }

  private ExecutableOutputField resolve(final ExecutableOutputType type, final String fieldName) {
    return type.field(fieldName)
        .orElseThrow(() -> type.missingFieldException(fieldName));
  }

  public static ExecutableSchemaBuilder builder() {
    return new ExecutableSchemaBuilder();
  }

  public static ExecutableSchema withRoot(final Type queryRoot) {
    return builder()
        .setRootType(GQLOpType.Query, queryRoot)
        .build();
  }

  public static ExecutableSchema withRoot(final Type queryRoot, final Type mutationRoot) {
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

  public ExecutableType resolveType(final String typeName) {
    return this.types.get(typeName);
  }

  /**
   * converts a registered java type to it's executable type.
   *
   * @param type
   * @return
   */

  public ExecutableType type(final TypeToken<? extends Object> type) {
    return this.tokens.get(type);
  }

}
