package io.zrz.graphql.zulu.engine;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import io.zrz.graphql.zulu.server.ZuluInjector;

/**
 * one instance of this is created per execution, regardless of the number of operations (e.g, in a batch).
 *
 * @author theo
 *
 */

public class ZuluExecutionScope {

  private final ZuluEngine engine;
  private ZuluInjector injector;
  private final Map<Type, ZuluScopedContext<?>> contexts = new HashMap<>();

  public ZuluExecutionScope() {
    this.engine = null;
  }

  public ZuluExecutionScope(final ZuluEngine engine, final ZuluInjector injector) {
    this.engine = engine;
    this.injector = injector;
  }

  public void complete(final ZuluExecutionResult[] res) {
    this.contexts.values().forEach(ZuluScopedContext::complete);
    System.err.println("compelted");
  }

  public void error(final Throwable t) {
    System.err.println("failed");
    this.contexts.values().forEach(ZuluScopedContext::error);
  }

  public Object context(final Type type) {

    final ZuluScopedContext<?> existing = this.contexts.get(type);

    if (existing != null) {
      return existing.get();
    }

    final ZuluExecutionScopeProvider<?> provider = this.injector.contextProvider(type);

    if (provider == null) {
      throw new IllegalArgumentException("no provider for " + type);
    }

    final ZuluScopedContext<?> value = provider.createContextValue(type);

    if (value == null) {
      throw new IllegalArgumentException("no context value");
    }

    this.contexts.put(type, value);

    return value.get();

  }

}
