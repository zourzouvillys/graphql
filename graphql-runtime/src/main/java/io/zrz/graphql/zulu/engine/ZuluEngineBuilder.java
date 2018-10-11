package io.zrz.graphql.zulu.engine;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import org.eclipse.jdt.annotation.NonNull;

import io.zrz.graphql.core.doc.GQLOpType;
import io.zrz.graphql.core.runtime.GQLOperationType;
import io.zrz.graphql.zulu.api.ZuluTypeBinder;
import io.zrz.graphql.zulu.api.ZuluTypeLoader;
import io.zrz.graphql.zulu.doc.GQLDocumentManager;
import io.zrz.graphql.zulu.executable.ExecutableSchema;
import io.zrz.graphql.zulu.executable.ExecutableSchemaBuilder;
import io.zrz.graphql.zulu.plugins.ZuluPlugin;

public class ZuluEngineBuilder {

  private final List<ZuluPlugin> plugins = new LinkedList<>();
  private final ExecutableSchemaBuilder schema = ExecutableSchema.builder();
  private GQLDocumentManager docmgr = null;
  private ZuluTypeBinder typeBinder;

  public ZuluEngineBuilder queryRoot(final Type type) {
    return this.addRoot(GQLOpType.Query, type);
  }

  public ZuluEngineBuilder mutationRoot(final Type type) {
    return this.addRoot(GQLOpType.Mutation, type);
  }

  public ZuluEngineBuilder subscriptionRoot(final Type type) {
    return this.addRoot(GQLOpType.Subscription, type);
  }

  public ZuluEngineBuilder addRoot(final GQLOperationType op, final Type type) {
    this.schema.setRootType(op, type);
    return this;
  }

  public ZuluEngineBuilder type(final Class<?> type) {
    this.schema.addType(type);
    this.schema.registerExtension(type);
    return this;
  }

  public ZuluEngineBuilder extension(final Class<@NonNull ?> type) {
    this.schema.registerExtension(type);
    return this;
  }

  /**
   * declares a type as an object, without scanning it.
   *
   * the resulting type will have no methods initially, they must be added manually or through extensions.
   *
   */

  public ZuluEngineBuilder stubType(final Type type) {
    this.schema.addStubType(type, null, null);
    return this;
  }

  public ZuluEngineBuilder plugin(final ZuluPlugin plugin) {
    plugin.onPluginRegistered(this);
    this.plugins.add(plugin);
    return this;
  }

  public ZuluEngineBuilder schema(final Consumer<ExecutableSchemaBuilder> adapter) {
    adapter.accept(this.schema);
    return this;
  }

  public ZuluEngineBuilder documentManager(final GQLDocumentManager docmgr) {
    this.docmgr = docmgr;
    return this;
  }

  public ZuluEngine build() {

    this.plugins.forEach(plugin -> plugin.onBuilding(this));

    final ZuluEngine engine = new ZuluEngine(this.schema.build(false), this.docmgr);

    this.plugins.forEach(plugin -> plugin.onEngine(engine));

    return engine;
  }

  public ZuluEngineBuilder typeLoader(final ZuluTypeLoader typeLoader) {
    this.schema.typeLoader(typeLoader);
    return this;
  }

}
